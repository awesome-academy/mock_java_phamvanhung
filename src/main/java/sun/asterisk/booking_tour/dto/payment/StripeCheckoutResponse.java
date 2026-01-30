package sun.asterisk.booking_tour.dto.payment;

public class StripeCheckoutResponse {

    private String sessionId;
    private String checkoutUrl;

    public StripeCheckoutResponse() {
    }

    public StripeCheckoutResponse(String sessionId, String checkoutUrl) {
        this.sessionId = sessionId;
        this.checkoutUrl = checkoutUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}
