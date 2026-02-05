package sun.asterisk.booking_tour.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.config.CustomUserDetails;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Force initialization of role within transaction
        String roleName = null;
        if (user.getRole() != null) {
            roleName = user.getRole().getName();
        }
        
        
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .isVerified(user.getIsVerified())
                .status(user.getStatus())
                .roleName(roleName)
                .build();
        
        return userDetails;
    }
}
