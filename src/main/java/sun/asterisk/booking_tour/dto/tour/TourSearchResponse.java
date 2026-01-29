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
@Schema(description = "Tour search result item")
public class TourSearchResponse {

    @Schema(description = "Tour ID", example = "1")
    private Long id;

    @Schema(description = "Tour name", example = "Tour Phú Quốc 3 ngày 2 đêm")
    private String name;

    @Schema(description = "Tour title", example = "Khám phá thiên đường biển đảo")
    private String title;

    @Schema(description = "Tour slug", example = "tour-phu-quoc-3-ngay-2-dem")
    private String slug;

    @Schema(description = "Tour thumbnail URL", example = "https://example.com/tour1.jpg")
    private String thumbnailUrl;

    @Schema(description = "Departure location", example = "Hà Nội")
    private String departureLocation;

    @Schema(description = "Main destination", example = "Phú Quốc")
    private String mainDestination;

    @Schema(description = "Duration in days", example = "3")
    private Integer durationDays;

    @Schema(description = "Duration in nights", example = "2")
    private Integer durationNights;

    @Schema(description = "Price for adult", example = "3500000")
    private BigDecimal priceAdult;

    @Schema(description = "Price for child", example = "2500000")
    private BigDecimal priceChild;

    @Schema(description = "Discount rate", example = "10.00")
    private BigDecimal discountRate;

    @Schema(description = "Final price after discount", example = "3150000")
    private BigDecimal finalPrice;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category name", example = "Tour biển đảo")
    private String categoryName;

    @Schema(description = "Average rating (1-5)", example = "4.5")
    private Double averageRating;

    @Schema(description = "Total reviews count", example = "25")
    private Long totalReviews;

    @Schema(description = "Total likes count", example = "100")
    private Long totalLikes;

    @Schema(description = "Has available slots", example = "true")
    private Boolean hasAvailableSlots;

    @Schema(description = "Next departure date (earliest available)", example = "2026-02-15")
    private LocalDate nextDepartureDate;
}
