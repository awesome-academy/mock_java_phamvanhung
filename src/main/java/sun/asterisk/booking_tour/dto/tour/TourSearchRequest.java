package sun.asterisk.booking_tour.dto.tour;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Tour search criteria")
public class TourSearchRequest {

    @Schema(description = "Keyword to search in name, title, description, destination", example = "Phu Quoc")
    private String keyword;

    @Schema(description = "Departure location", example = "Ha Noi")
    private String departureLocation;

    @Schema(description = "Main destination", example = "Phu Quoc")
    private String destination;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Minimum price (adult)", example = "1000000")
    private BigDecimal minPrice;

    @Schema(description = "Maximum price (adult)", example = "5000000")
    private BigDecimal maxPrice;

    @Schema(description = "Minimum duration in days", example = "2")
    private Integer minDuration;

    @Schema(description = "Maximum duration in days", example = "7")
    private Integer maxDuration;

    @Schema(description = "Departure date from", example = "2026-02-01")
    private LocalDate departureFrom;

    @Schema(description = "Departure date to", example = "2026-03-01")
    private LocalDate departureTo;

    @Schema(description = "Minimum rating (1-5)", example = "4")
    private Integer minRating;

    @Schema(description = "Only tours with discount", example = "true")
    private Boolean hasDiscount;

    @Schema(description = "Only tours with available slots", example = "true")
    private Boolean hasAvailableSlots;

    @Schema(description = "Sort by: price, rating, popular, newest, discount", example = "price")
    @Builder.Default
    private String sortBy = "newest";

    @Schema(description = "Sort order: asc, desc", example = "asc")
    @Builder.Default
    private String sortOrder = "desc";

    @Schema(description = "Page number (0-based)", example = "0")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Page size", example = "10")
    @Builder.Default
    private Integer size = 10;
}
