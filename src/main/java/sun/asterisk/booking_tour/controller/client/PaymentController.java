package sun.asterisk.booking_tour.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import sun.asterisk.booking_tour.dto.payment.StripeCheckoutResponse;
import sun.asterisk.booking_tour.dto.payment.StripePaymentStatusResponse;
import sun.asterisk.booking_tour.dto.payment.StripeCheckoutRequest;
import sun.asterisk.booking_tour.service.PaymentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe/checkout")
    public ResponseEntity<StripeCheckoutResponse> createStripeCheckout(@Valid @RequestBody StripeCheckoutRequest request) {
        StripeCheckoutResponse response = paymentService.createStripeCheckout(request.getBookingCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/success")
    public ResponseEntity<StripePaymentStatusResponse> stripeSuccess(
            @RequestParam("session_id") String sessionId) {

        StripePaymentStatusResponse response = paymentService.handleStripeSuccess(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/cancel")
    public ResponseEntity<StripePaymentStatusResponse> stripeCancel(
            @RequestParam("session_id") String sessionId) {

        StripePaymentStatusResponse response = paymentService.handleStripeCancel(sessionId);
        return ResponseEntity.ok(response);
    }
}
