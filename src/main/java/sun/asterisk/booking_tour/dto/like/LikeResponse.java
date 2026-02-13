package sun.asterisk.booking_tour.dto.like;

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
@Schema(description = "Like response")
public class LikeResponse {

    @Schema(description = "Success status", example = "true")
    private Boolean success;

    @Schema(description = "Is liked", example = "true")
    private Boolean isLiked;

    @Schema(description = "Total likes", example = "10")
    private Integer totalLikes;

    @Schema(description = "Message", example = "Liked successfully")
    private String message;
}
