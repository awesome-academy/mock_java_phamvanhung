package sun.asterisk.booking_tour.dto.category;

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
@Schema(description = "Category create/update request")
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Category name", example = "Mountain Tours")
    private String name;

    @Schema(description = "Category description", example = "Tours exploring the northern mountains")
    private String description;
}
