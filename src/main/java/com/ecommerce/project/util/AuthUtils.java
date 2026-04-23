package com.ecommerce.project.util;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.security.repository.UserRepository;
import com.ecommerce.project.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    @Autowired
    private UserRepository userRepository;

    public String loggedInEmail() {
        return principal().getEmail();
    }

    public Long loggedInUserId() {
        return principal().getId();
    }

    public User loggedInUser() {
        Long id = loggedInUserId();
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserDetailsImpl principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            throw new APIException("No authenticated user found");
        }
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
