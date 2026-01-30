package sun.asterisk.booking_tour.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import sun.asterisk.booking_tour.entity.Booking;

@Service
public class EmailQueueService {

    private static final Logger logger = LoggerFactory.getLogger(EmailQueueService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${mail.queue.redis.key:mail:queue}")
    private String queueKey;

    public EmailQueueService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void enqueueBookingPaymentSuccess(Booking booking) {
        if (booking == null || booking.getCode() == null || booking.getCode().isBlank()) {
            return;
        }

        String bookingCode = booking.getCode();

        RedisEmailMessage message = new RedisEmailMessage();
        message.setType(RedisEmailMessage.TYPE_BOOKING_PAYMENT_SUCCESS);
        message.setBookingCode(bookingCode);
        message.setAttempt(0);

        try {
            String payload = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(queueKey, payload);
            logger.warn("Enqueued email message to Redis. queueKey={}, type={}, bookingCode={}",
                    queueKey, message.getType(), bookingCode);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize RedisEmailMessage", e);
        }
    }

    public static class RedisEmailMessage {
        public static final String TYPE_BOOKING_PAYMENT_SUCCESS = "BOOKING_PAYMENT_SUCCESS";

        private String type;
        private String bookingCode;
        private int attempt;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBookingCode() {
            return bookingCode;
        }

        public void setBookingCode(String bookingCode) {
            this.bookingCode = bookingCode;
        }

        public int getAttempt() {
            return attempt;
        }

        public void setAttempt(int attempt) {
            this.attempt = attempt;
        }
    }
}
