package sun.asterisk.booking_tour.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.dto.booking.CreateBookingRequest;
import sun.asterisk.booking_tour.dto.booking.CreateBookingResponse;
import sun.asterisk.booking_tour.service.BookingService;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "API endpoints for booking tours")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create booking", description = "Create a booking for a specific tour departure")
    @CommonApiResponses.BadRequest
    @CommonApiResponses.NotFound
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successfully created booking",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CreateBookingResponse.class)
                )
        )
    })
    @PostMapping("")
    public ResponseEntity<CreateBookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        CreateBookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }
}
