package sun.asterisk.booking_tour.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.dto.user.UpdateProfileRequest;
import sun.asterisk.booking_tour.dto.user.UserProfileResponse;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user profile by user ID
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapToUserProfileResponse(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        updateUserFields(user, request);

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for userId: {}", userId);

        return mapToUserProfileResponse(updatedUser);
    }

    private void updateUserFields(User user, UpdateProfileRequest request) {
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .isVerified(user.getIsVerified())
                .status(user.getStatus())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
