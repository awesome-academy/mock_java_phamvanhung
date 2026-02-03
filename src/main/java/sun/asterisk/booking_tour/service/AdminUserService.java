package sun.asterisk.booking_tour.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.dto.user.AdminUserListResponse;
import sun.asterisk.booking_tour.dto.user.AdminUserRequest;
import sun.asterisk.booking_tour.dto.user.RoleDTO;
import sun.asterisk.booking_tour.entity.Role;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.enums.UserStatus;
import sun.asterisk.booking_tour.exception.BadRequestException;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.repository.RoleRepository;
import sun.asterisk.booking_tour.repository.UserRepository;
import sun.asterisk.booking_tour.specification.UserSpecification;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminUserListResponse> getAllUsers(String search, UserStatus status, Pageable pageable) {
        UserSpecification spec = new UserSpecification(search, status);
        
        return userRepository.findAll(spec, pageable)
                .map(this::mapToAdminUserListResponse);
    }

    @Transactional(readOnly = true)
    public AdminUserListResponse getUserById(Long id) {
        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        return mapToAdminUserListResponse(user);
    }

    @Transactional
    public AdminUserListResponse createUser(AdminUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Set default values
        user.setIsVerified(false);
        user.setStatus(UserStatus.ACTIVE);

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        } else {
            // Set default role to USER if not specified
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));
            user.setRole(userRole);
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return mapToAdminUserListResponse(savedUser);
    }

    @Transactional
    public AdminUserListResponse updateUser(Long id, AdminUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Only update allowed fields: name, email, password, role
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        return mapToAdminUserListResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Prevent self-deletion
        if (currentUserEmail != null && user.getEmail().equals(currentUserEmail)) {
            throw new BadRequestException("You cannot delete your own account");
        }

        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("id", role.getId());
                    roleMap.put("name", role.getName());
                    return roleMap;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getRoleIdByUserId(Long userId) {
        User user = userRepository.findByIdWithRole(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getRole() != null ? user.getRole().getId() : null;
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.INACTIVE);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);
        log.info("User status toggled successfully for id: {}", id);
    }

    private AdminUserListResponse mapToAdminUserListResponse(User user) {
        return AdminUserListResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .isVerified(user.getIsVerified())
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
