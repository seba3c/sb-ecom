package com.ecommerce.project.security.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.security.dto.LoginRequest;
import com.ecommerce.project.security.dto.MessageResponse;
import com.ecommerce.project.security.dto.SignupRequest;
import com.ecommerce.project.security.dto.UserInfoResponse;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.repository.RoleRepository;
import com.ecommerce.project.security.repository.UserRepository;
import com.ecommerce.project.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Username is already taken!")
            );
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Email is already taken!")
            );
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword())
        );

        user.setRoles(resolveRoles(signupRequest.getRoles()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private Set<Role> resolveRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            roles.add(roleRepository.findByName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(roleRepository.findByName(AppRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                    case "seller" -> roles.add(roleRepository.findByName(AppRole.ROLE_SELLER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                    default -> roles.add(roleRepository.findByName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                }
            });
        }
        return roles;
    }
}
