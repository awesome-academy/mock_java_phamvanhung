package sun.asterisk.booking_tour.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.asterisk.booking_tour.dto.common.FileUploadResponse;
import sun.asterisk.booking_tour.exception.FileUploadException;
import sun.asterisk.booking_tour.exception.ValidationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.base-url}")
    private String baseUrl;

    public FileUploadResponse uploadAvatar(MultipartFile file) {
        validateImageFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        Path uploadPath = createUploadDirectory();
        Path filePath = uploadPath.resolve(fileName);

        saveFile(file, filePath);

        String fileUrl = baseUrl + "/avatars/" + fileName;

        return FileUploadResponse.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .build();
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size must not exceed 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException("Unsupported image type");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ValidationException("File content is not a valid image");
            }
        } catch (IOException e) {
            log.error("Failed to read image content", e);
            throw new ValidationException("Failed to validate image content");
        }

        if (image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new ValidationException("Invalid image dimensions");
        }

    }

    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }

        return filename.substring(lastDotIndex);
    }

    private Path createUploadDirectory() {
        Path uploadPath = Paths.get(uploadDir, "avatars");

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Failed to create upload directory: {}", uploadPath, e);
                throw new FileUploadException("Failed to create upload directory", e);
            }
        }

        return uploadPath;
    }

    private void saveFile(MultipartFile file, Path filePath) {
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", filePath, e);
            throw new FileUploadException("Failed to save file");
        }
    }
}
