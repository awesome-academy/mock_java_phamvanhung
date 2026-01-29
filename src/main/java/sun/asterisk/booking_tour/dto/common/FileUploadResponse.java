package sun.asterisk.booking_tour.dto.common;

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
@Schema(description = "File upload response")
public class FileUploadResponse {

    @Schema(description = "Uploaded file URL", example = "https://example.com/uploads/avatar.jpg")
    private String fileUrl;

    @Schema(description = "Original filename", example = "avatar.jpg")
    private String fileName;

    @Schema(description = "File size in bytes", example = "1024000")
    private Long fileSize;

    @Schema(description = "Content type", example = "image/jpeg")
    private String contentType;
}
