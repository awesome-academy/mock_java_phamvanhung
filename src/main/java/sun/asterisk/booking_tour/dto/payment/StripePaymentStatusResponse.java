package sun.asterisk.booking_tour.dto.payment;

public class StripePaymentStatusResponse {

    private boolean success;
    private String message;
    private String bookingCode;
    private String sessionId;

    public StripePaymentStatusResponse() {
    }

    public StripePaymentStatusResponse(boolean success, String message, String bookingCode, String sessionId) {
        this.success = success;
        this.message = message;
        this.bookingCode = bookingCode;
        this.sessionId = sessionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
