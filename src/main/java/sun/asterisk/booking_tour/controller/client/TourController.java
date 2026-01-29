package sun.asterisk.booking_tour.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.dto.common.PageResponse;
import sun.asterisk.booking_tour.dto.tour.TourDetailResponse;
import sun.asterisk.booking_tour.dto.tour.TourSearchRequest;
import sun.asterisk.booking_tour.dto.tour.TourSearchResponse;
import sun.asterisk.booking_tour.service.TourService;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
@Tag(name = "Tour", description = "API endpoints for tour management")
public class TourController {

    private final TourService tourService;

    @Operation(
            summary = "Search tours",
            description = "Search and filter tours with various criteria. All parameters are optional. This endpoint does not require authentication."
    )
    @CommonApiResponses.BadRequest
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved tours",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PageResponse.class)
                )
        )
    })
    @GetMapping("")
    public ResponseEntity<PageResponse<TourSearchResponse>> searchTours(
            @Valid @ModelAttribute TourSearchRequest request
    ) {
        PageResponse<TourSearchResponse> response = tourService.searchTours(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get tour by slug",
            description = "Retrieve detailed information about a specific tour by its slug. This endpoint does not require authentication."
    )
    @CommonApiResponses.NotFound
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved tour details",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TourDetailResponse.class)
                )
        )
    })
    @GetMapping("/{slug}")
    public ResponseEntity<TourDetailResponse> getTourBySlug(
            @Parameter(description = "Tour slug", example = "tour-phu-quoc-3n2d", required = true)
            @PathVariable String slug
    ) {
        TourDetailResponse response = tourService.getTourBySlug(slug);
        return ResponseEntity.ok(response);
    }
}
