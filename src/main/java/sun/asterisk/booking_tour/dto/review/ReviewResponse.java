package sun.asterisk.booking_tour.dto.review;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.asterisk.booking_tour.dto.user.UserBasicResponse;
import sun.asterisk.booking_tour.enums.ReviewStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Review response")
public class ReviewResponse {

    @Schema(description = "Review ID", example = "1")
    private Long id;

    @Schema(description = "User information")
    private UserBasicResponse user;

    @Schema(description = "Tour ID", example = "1")
    private Long tourId;

    @Schema(description = "Tour name", example = "Tour Phú Quốc 3N2Đ")
    private String tourName;

    @Schema(description = "Booking ID", example = "1")
    private Long bookingId;

    @Schema(description = "Booking code", example = "BK123456")
    private String bookingCode;

    @Schema(description = "Rating (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "Review title", example = "Amazing experience!")
    private String title;

    @Schema(description = "Review content", example = "This tour was absolutely amazing...")
    private String content;

    @Schema(description = "Review status", example = "APPROVED")
    private ReviewStatus status;

    @Schema(description = "Number of comments", example = "5")
    private Integer commentCount;

    @Schema(description = "Number of likes", example = "10")
    private Integer likeCount;

    @Schema(description = "Is liked by current user", example = "true")
    private Boolean isLiked;

    @Schema(description = "Created at", example = "2026-01-30T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at", example = "2026-01-30T10:00:00")
    private LocalDateTime updatedAt;
}
