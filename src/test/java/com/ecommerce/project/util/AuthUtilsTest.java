package com.ecommerce.project.util;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.security.repository.UserRepository;
import com.ecommerce.project.security.service.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUtilsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthUtils authUtils;

    private UserDetailsImpl userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loggedInEmail_authenticatedUser_returnsEmail() {
        assertEquals("test@example.com", authUtils.loggedInEmail());
    }

    @Test
    void loggedInEmail_noAuthentication_throwsAPIException() {
        SecurityContextHolder.clearContext();
        assertThrows(APIException.class, () -> authUtils.loggedInEmail());
    }

    @Test
    void loggedInUserId_authenticatedUser_returnsId() {
        assertEquals(1L, authUtils.loggedInUserId());
    }

    @Test
    void loggedInUserId_noAuthentication_throwsAPIException() {
        SecurityContextHolder.clearContext();
        assertThrows(APIException.class, () -> authUtils.loggedInUserId());
    }

    @Test
    void loggedInUser_authenticatedUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = authUtils.loggedInUser();

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void loggedInUser_userNotFoundInDb_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authUtils.loggedInUser());
    }

    @Test
    void loggedInUser_noAuthentication_throwsAPIException() {
        SecurityContextHolder.clearContext();
        assertThrows(APIException.class, () -> authUtils.loggedInUser());
    }
}
