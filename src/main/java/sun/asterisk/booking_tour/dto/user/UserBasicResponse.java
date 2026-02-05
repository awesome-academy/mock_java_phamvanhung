package sun.asterisk.booking_tour.dto.user;

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
@Schema(description = "Basic user information")
public class UserBasicResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User avatar URL", example = "http://localhost:8080/uploads/avatars/user1.jpg")
    private String avatarUrl;
}
