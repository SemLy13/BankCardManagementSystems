package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.dto.MessageResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserDetailsServiceImpl;
import com.example.bankcards.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AuthController - аутентификация и авторизация
 */
@WebMvcTest(AuthController.class)
@Import(com.example.bankcards.TestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDetails userDetails;
    private Authentication authentication;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");
        testRole.setDescription("Regular user role");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$testHashedPassword");
        testUser.setEnabled(true);
        testUser.getRoles().add(testRole);

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("$2a$10$testHashedPassword")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void whenLoginWithValidCredentials_thenReturnJwtToken() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(authentication)).thenReturn("test-jwt-token");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void whenLoginWithInvalidCredentials_thenReturnUnauthorized() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenLoginWithInvalidRequestData_thenReturnBadRequest() throws Exception {
        // given - пустой запрос
        String invalidJson = "{}";

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLogout_thenReturnSuccessMessage() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You've been signed out!"));
    }

    @Test
    void whenLoginWithMissingUsername_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithMissingPassword_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithNullCredentials_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithTooShortUsername_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ab"); // Too short (minimum 3 characters)
        loginRequest.setPassword("password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithTooShortPassword_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("12345"); // Too short (minimum 6 characters)

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithInvalidEmailFormat_thenReturnBadRequest() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("invalid-email-format"); // Invalid email format for username
        loginRequest.setPassword("password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
