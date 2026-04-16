package com.ecommerce.project.security.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.security.dto.LoginRequest;
import com.ecommerce.project.security.dto.SignupRequest;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.repository.RoleRepository;
import com.ecommerce.project.security.repository.UserRepository;
import com.ecommerce.project.security.service.UserDetailsImpl;
import com.ecommerce.project.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void signin_validCredentials_returns200WithToken() throws Exception {
        UserDetailsImpl principal = new UserDetailsImpl(1L, "alice", "alice@example.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateTokenFromUsername(principal)).thenReturn("jwt-token-here");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("alice");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.jwtToken").value("jwt-token-here"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void signin_badCredentials_returns404WithMessage() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("alice");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    void signup_newUser_returns200WithSuccessMessage() throws Exception {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password1")).thenReturn("encoded");
        when(roleRepository.findByName(AppRole.ROLE_USER))
                .thenReturn(Optional.of(new Role(AppRole.ROLE_USER)));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("bob");
        signupRequest.setEmail("bob@example.com");
        signupRequest.setPassword("password1");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void signup_duplicateUsername_returns400() throws Exception {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("alice");
        signupRequest.setEmail("alice@example.com");
        signupRequest.setPassword("password1");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    void signup_duplicateEmail_returns400() throws Exception {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("alice");
        signupRequest.setEmail("alice@example.com");
        signupRequest.setPassword("password1");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    void signup_withAdminRole_assignsRoleAdmin() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("adminpass")).thenReturn("encoded");
        when(roleRepository.findByName(AppRole.ROLE_ADMIN))
                .thenReturn(Optional.of(new Role(AppRole.ROLE_ADMIN)));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("admin");
        signupRequest.setEmail("admin@example.com");
        signupRequest.setPassword("adminpass");
        signupRequest.setRoles(Set.of("admin"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void signup_withSellerRole_assignsRoleSeller() throws Exception {
        when(userRepository.existsByUsername("seller")).thenReturn(false);
        when(userRepository.existsByEmail("seller@example.com")).thenReturn(false);
        when(passwordEncoder.encode("sellerpass")).thenReturn("encoded");
        when(roleRepository.findByName(AppRole.ROLE_SELLER))
                .thenReturn(Optional.of(new Role(AppRole.ROLE_SELLER)));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("seller");
        signupRequest.setEmail("seller@example.com");
        signupRequest.setPassword("sellerpass");
        signupRequest.setRoles(Set.of("seller"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
}
