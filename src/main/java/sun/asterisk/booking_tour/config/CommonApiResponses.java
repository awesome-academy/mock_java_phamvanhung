package sun.asterisk.booking_tour.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import sun.asterisk.booking_tour.dto.user.UserProfileResponse;

/**
 * Common reusable API response definitions for consistent error handling across endpoints
 */
public class CommonApiResponses {

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileResponse.class)
            )
    )
    public @interface UserProfileSuccess {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing authentication token",
                content = @Content(mediaType = "application/json")
        )
    })
    public @interface Unauthorized {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "403",
                description = "Forbidden - User does not have required role",
                content = @Content(mediaType = "application/json")
        )
    })
    public @interface Forbidden {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(mediaType = "application/json")
        )
    })
    public @interface UserNotFound {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = @Content(mediaType = "application/json")
        )
    })
    public @interface BadRequest {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "Invalid file format or size",
                content = @Content(mediaType = "application/json")
        )
    })
    public @interface InvalidFile {}
}
