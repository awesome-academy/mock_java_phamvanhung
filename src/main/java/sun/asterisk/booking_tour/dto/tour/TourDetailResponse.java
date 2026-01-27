package sun.asterisk.booking_tour.dto.tour;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
@Schema(description = "Tour detail information")
public class TourDetailResponse {

    @Schema(description = "Tour ID", example = "1")
    private Long id;

    @Schema(description = "Tour name", example = "Tour Phu Quoc 3 ngay 2 dem")
    private String name;

    @Schema(description = "Tour title", example = "Kham pha thien duong bien dao")
    private String title;

    @Schema(description = "Tour slug", example = "tour-phu-quoc-3-ngay-2-dem")
    private String slug;

    @Schema(description = "Tour description")
    private String description;

    @Schema(description = "Tour itinerary")
    private String itinerary;

    @Schema(description = "Thumbnail URL", example = "https://example.com/tour1.jpg")
    private String thumbnailUrl;

    @Schema(description = "Departure location", example = "Ha Noi")
    private String departureLocation;

    @Schema(description = "Main destination", example = "Phu Quoc")
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

    @Schema(description = "Category information")
    private CategoryInfo category;

    @Schema(description = "Creator information")
    private CreatorInfo creator;

    @Schema(description = "Average rating (1-5)", example = "4.5")
    private Double averageRating;

    @Schema(description = "Total reviews count", example = "25")
    private Long totalReviews;

    @Schema(description = "Total likes count", example = "100")
    private Long totalLikes;

    @Schema(description = "Tour images")
    private List<TourImageInfo> images;

    @Schema(description = "Upcoming departures")
    private List<DepartureInfo> upcomingDepartures;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Category information")
    public static class CategoryInfo {
        @Schema(description = "Category ID", example = "1")
        private Long id;

        @Schema(description = "Category name", example = "Tour bien dao")
        private String name;

        @Schema(description = "Category slug", example = "tour-bien-dao")
        private String slug;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Creator information")
    public static class CreatorInfo {
        @Schema(description = "Creator ID", example = "1")
        private Long id;

        @Schema(description = "Creator name", example = "Admin System")
        private String name;

        @Schema(description = "Creator email", example = "admin@example.com")
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tour image information")
    public static class TourImageInfo {
        @Schema(description = "Image ID", example = "1")
        private Long id;

        @Schema(description = "Image URL", example = "https://example.com/image.jpg")
        private String imageUrl;

        @Schema(description = "Image caption", example = "Beautiful beach view")
        private String caption;

        @Schema(description = "Is primary image", example = "true")
        private Boolean isPrimary;

        @Schema(description = "Display order", example = "1")
        private Integer displayOrder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tour departure information")
    public static class DepartureInfo {
        @Schema(description = "Departure ID", example = "1")
        private Long id;

        @Schema(description = "Departure date", example = "2026-02-15")
        private LocalDate departureDate;

        @Schema(description = "Return date", example = "2026-02-17")
        private LocalDate returnDate;

        @Schema(description = "Total slots", example = "30")
        private Integer totalSlots;

        @Schema(description = "Available slots", example = "25")
        private Integer availableSlots;

        @Schema(description = "Departure status", example = "OPEN")
        private String status;
    }
}
