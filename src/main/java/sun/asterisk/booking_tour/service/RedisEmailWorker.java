package sun.asterisk.booking_tour.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import sun.asterisk.booking_tour.entity.Booking;
import sun.asterisk.booking_tour.repository.BookingRepository;

@Service
public class RedisEmailWorker {

    private static final Logger logger = LoggerFactory.getLogger(RedisEmailWorker.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @Value("${mail.queue.redis.key:mail:queue}")
    private String queueKey;

    @Value("${mail.queue.pop-timeout-ms:1000}")
    private long popTimeoutMs;

    @Value("${mail.queue.max-attempts:5}")
    private int maxAttempts;

    @Value("${mail.queue.base-backoff-ms:5000}")
    private long baseBackoffMs;

    private final String delayedQueueKey;

    {
        // Use a postfix for delayed queue key
        delayedQueueKey = queueKey + ":delayed";
    }

    public RedisEmailWorker(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            BookingRepository bookingRepository,
            EmailService emailService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelayString = "${mail.queue.worker-interval-ms:200}")
    public void poll() {
        try {
            // First, move due delayed messages to the main queue
            moveDueDelayedMessages();

            String payload = redisTemplate.opsForList().rightPop(queueKey, Duration.ofMillis(popTimeoutMs));
            if (payload == null || payload.isBlank()) {
                return;
            }

            EmailQueueService.RedisEmailMessage message =
                    objectMapper.readValue(payload, EmailQueueService.RedisEmailMessage.class);

            handle(message);

        } catch (Exception e) {
            logger.error("RedisEmailWorker poll failed", e);
        }
    }

    private void moveDueDelayedMessages() {
        long now = Instant.now().toEpochMilli();
        // Get all messages with score <= now
        Set<String> dueMessages = redisTemplate.opsForZSet().rangeByScore(delayedQueueKey, 0, now);
        if (dueMessages != null && !dueMessages.isEmpty()) {
            for (String msg : dueMessages) {
                // Remove from delayed queue and push to main queue
                Long removed = redisTemplate.opsForZSet().remove(delayedQueueKey, msg);
                if (removed != null && removed > 0) {
                    redisTemplate.opsForList().leftPush(queueKey, msg);
                }
            }
        }
    }

    private void handle(EmailQueueService.RedisEmailMessage message) {
        if (message == null) {
            return;
        }

        String type = message.getType();
        String bookingCode = message.getBookingCode();
        int attempt = message.getAttempt();

        if (type == null || type.isBlank() || bookingCode == null || bookingCode.isBlank()) {
            logger.warn("Skip invalid redis email message. type={}, bookingCode={}", type, bookingCode);
            return;
        }

        try {
            if (EmailQueueService.RedisEmailMessage.TYPE_BOOKING_PAYMENT_SUCCESS.equals(type)) {
                Booking booking = bookingRepository.findByCodeWithDepartureAndTour(bookingCode)
                        .orElseThrow(() -> new IllegalStateException("Booking not found: " + bookingCode));

                emailService.sendBookingPaymentSuccessEmail(booking);
                logger.warn("Processed redis email message. type={}, bookingCode={}, attempt={}", type, bookingCode, attempt);
                return;
            }

            throw new IllegalStateException("Unsupported email message type: " + type);

        } catch (Exception e) {
            int nextAttempt = attempt + 1;
            if (nextAttempt >= maxAttempts) {
                logger.error("Redis email message failed (max attempts reached). type={}, bookingCode={}, attempt={}",
                        type, bookingCode, nextAttempt, e);
                return;
            }

            long delayMs = computeBackoffMs(nextAttempt);
            logger.error("Redis email message failed, will retry. type={}, bookingCode={}, attempt={}, delayMs={}",
                    type, bookingCode, nextAttempt, delayMs, e);

            message.setAttempt(nextAttempt);
            try {
                String payload = objectMapper.writeValueAsString(message);
                long nextTime = Instant.now().toEpochMilli() + delayMs;
                redisTemplate.opsForZSet().add(delayedQueueKey, payload, nextTime);
            } catch (Exception ex) {
                logger.error("Failed to requeue redis email message (delayed). type={}, bookingCode={}, attempt={}",
                        type, bookingCode, nextAttempt, ex);
            }
        }
    }

    private long computeBackoffMs(int attempt) {
        long multiplier = 1L << Math.max(0, attempt - 1);
        long delay = baseBackoffMs * multiplier;
        return Math.min(delay, 300_000L);
    }
}
