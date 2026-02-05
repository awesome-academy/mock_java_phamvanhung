package sun.asterisk.booking_tour.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Update review request - all fields are optional")
public class UpdateReviewRequest {

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Schema(description = "Rating (1-5) - optional", example = "5")
    private Integer rating;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Schema(description = "Review title - optional", example = "Amazing experience!")
    private String title;

    @Size(min = 10, message = "Content must be at least 10 characters")
    @Schema(description = "Review content - optional", example = "This tour was absolutely amazing. The guide was very professional...")
    private String content;
}
