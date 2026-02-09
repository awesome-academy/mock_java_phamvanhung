package sun.asterisk.booking_tour.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Create comment request")
public class CreateCommentRequest {

    @Schema(description = "Tour ID (for comment on tour)", example = "1")
    private Long tourId;

    @Schema(description = "Review ID (for comment on review)", example = "1")
    private Long reviewId;

    @Schema(description = "Parent comment ID (for reply)", example = "1")
    private Long parentId;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    @Schema(description = "Comment content", example = "Great review! I totally agree.")
    private String content;
}
