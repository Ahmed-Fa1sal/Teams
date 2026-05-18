package com.teams.teams.security;

import com.teams.teams.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to load by username first
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        
        // If not found by username, try by email (supports email-based login)
        if (isEmail(username)) {
            user = userRepository.findByEmail(username);
            if (user.isPresent()) {
                return user.get();
            }
        }
        
        throw new UsernameNotFoundException("User not found: " + username);
    }

    /**
     * Helper method to check if a string looks like an email address.
     */
    private boolean isEmail(String input) {
        return input != null && input.contains("@");
    }
}

