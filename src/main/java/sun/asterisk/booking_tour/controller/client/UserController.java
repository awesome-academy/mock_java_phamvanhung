package sun.asterisk.booking_tour.controller.client;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.config.CustomUserDetails;
import sun.asterisk.booking_tour.dto.common.FileUploadResponse;
import sun.asterisk.booking_tour.dto.user.UpdateProfileRequest;
import sun.asterisk.booking_tour.dto.user.UserProfileResponse;
import sun.asterisk.booking_tour.service.FileUploadService;
import sun.asterisk.booking_tour.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API endpoints for user management")
public class UserController {

    private final UserService userService;
    private final FileUploadService fileUploadService;

    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the profile information of the currently authenticated user. Requires a valid JWT access token in the Authorization header (Bearer token).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @CommonApiResponses.UserProfileSuccess
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.UserNotFound
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .id(userDetails.getUserId())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .phone(userDetails.getPhone())
                .dateOfBirth(userDetails.getDateOfBirth())
                .avatarUrl(userDetails.getAvatarUrl())
                .isVerified(userDetails.getIsVerified())
                .status(userDetails.getStatus())
                .role(userDetails.getRoleName())
                .build();

        return ResponseEntity.ok(userProfile);
    }

    @Operation(
            summary = "Update user profile",
            description = "Update the profile information of the currently authenticated user. Requires USER role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @CommonApiResponses.UserProfileSuccess
    @CommonApiResponses.BadRequest
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.Forbidden
    @CommonApiResponses.UserNotFound
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse response = userService.updateProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Upload avatar image",
            description = "Upload a new avatar image for the user. Allowed formats: JPG, PNG, WEBP. Max size: 2MB.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully uploaded avatar",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FileUploadResponse.class)
            )
    )
    @CommonApiResponses.InvalidFile
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.Forbidden
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        FileUploadResponse response = fileUploadService.uploadAvatar(file);
        return ResponseEntity.ok(response);
    }
}
