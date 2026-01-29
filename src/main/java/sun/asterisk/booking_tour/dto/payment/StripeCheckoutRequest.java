package sun.asterisk.booking_tour.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Stripe checkout request")
public class StripeCheckoutRequest {

    @NotBlank
    @Schema(description = "Booking code", example = "BK123456")
    private String bookingCode;

}
