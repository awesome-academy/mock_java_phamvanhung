package sun.asterisk.booking_tour.service;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import sun.asterisk.booking_tour.entity.Booking;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.TourDeparture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:}")
    private String from;

    @Value("${spring.mail.host:}")
    private String host;

    @Value("${spring.mail.port:0}")
    private Integer port;

    @Value("${spring.mail.properties.mail.smtp.auth:false}")
    private Boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    private Boolean starttlsEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingPaymentSuccessEmail(Booking booking) {
        if (booking == null) {
            logger.warn("Skip sending email: booking is null");
            return;
        }
        if (booking.getContactEmail() == null || booking.getContactEmail().isBlank()) {
            logger.warn("Skip sending email: contactEmail is blank. bookingCode={}", booking.getCode());
            return;
        }

        TourDeparture departure = booking.getTourDeparture();
        Tour tour = departure != null ? departure.getTour() : null;

        String subject = "Payment success - Booking " + booking.getCode();

        String departureDate = departure != null && departure.getDepartureDate() != null
                ? departure.getDepartureDate().format(DATE_FORMAT)
                : "";
        String returnDate = departure != null && departure.getReturnDate() != null
                ? departure.getReturnDate().format(DATE_FORMAT)
                : "";

        String tourName = tour != null && tour.getName() != null ? tour.getName() : "";

        StringBuilder text = new StringBuilder();
        text.append("Your payment was successful.\n\n");
        text.append("Booking code: ").append(booking.getCode()).append("\n");
        text.append("Tour: ").append(tourName).append("\n");
        text.append("Departure date: ").append(departureDate).append("\n");
        text.append("Return date: ").append(returnDate).append("\n");
        text.append("Passengers: ")
                .append((booking.getNumAdults() != null ? booking.getNumAdults() : 0))
                .append(" adults, ")
                .append((booking.getNumChildren() != null ? booking.getNumChildren() : 0))
                .append(" children\n");
        text.append("Total: ").append(booking.getFinalTotal()).append("\n\n");
        text.append("Thank you for your booking.");

        SimpleMailMessage message = new SimpleMailMessage();
        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }
        message.setTo(booking.getContactEmail());
        message.setSubject(subject);
        message.setText(text.toString());

        logger.warn(
                "Attempting to send email. smtpHost={}, smtpPort={}, smtpAuth={}, starttlsEnabled={}, from={}, to={}, subject={}",
                host,
                port,
                smtpAuth,
                starttlsEnabled,
                from,
                booking.getContactEmail(),
                subject);

        try {
            mailSender.send(message);
            logger.warn("Email send call completed. to={}, subject={}", booking.getContactEmail(), subject);
        } catch (Exception e) {
            logger.error("Email send failed. to={}, subject={}", booking.getContactEmail(), subject, e);
            throw e;
        }
    }
}
