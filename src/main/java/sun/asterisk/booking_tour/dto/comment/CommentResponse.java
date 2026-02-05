package sun.asterisk.booking_tour.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.asterisk.booking_tour.dto.user.UserBasicResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Comment response")
public class CommentResponse {

    @Schema(description = "Comment ID", example = "1")
    private Long id;

    @Schema(description = "User information")
    private UserBasicResponse user;

    @Schema(description = "Tour ID", example = "1")
    private Long tourId;

    @Schema(description = "Review ID", example = "1")
    private Long reviewId;

    @Schema(description = "Parent comment ID", example = "1")
    private Long parentId;

    @Schema(description = "Comment content", example = "Great review!")
    private String content;

    @Schema(description = "Reply count", example = "3")
    private Integer replyCount;

    @Schema(description = "Replies")
    private List<CommentResponse> replies;

    @Schema(description = "Created at", example = "2026-01-30T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at", example = "2026-01-30T10:00:00")
    private LocalDateTime updatedAt;
}
