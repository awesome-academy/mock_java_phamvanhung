package sun.asterisk.booking_tour.dto.user;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
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
@Schema(description = "Update user profile request")
public class UpdateProfileRequest {

    @Schema(description = "First name", example = "John")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-01")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Schema(description = "Avatar URL (will be set after file upload)", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
}
