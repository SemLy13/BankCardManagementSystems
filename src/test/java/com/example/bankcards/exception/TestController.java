package com.example.bankcards.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/illegal-argument")
    public void testIllegalArgumentException() {
        throw new IllegalArgumentException("Invalid argument");
    }

    @GetMapping("/resource-not-found")
    public void testResourceNotFoundException() {
        throw new ResourceNotFoundException("Resource not found");
    }

    @GetMapping("/business-exception")
    public void testBusinessException() {
        throw new BusinessException("CUSTOM_ERROR", "Custom business error", org.springframework.http.HttpStatus.CONFLICT);
    }

    @GetMapping("/bad-credentials")
    public void testBadCredentialsException() {
        throw new org.springframework.security.authentication.BadCredentialsException("Bad credentials");
    }

    @GetMapping("/access-denied")
    public void testAccessDeniedException() {
        throw new org.springframework.security.access.AccessDeniedException("Access denied");
    }

    @GetMapping("/validation-error")
    public void testMethodArgumentNotValidException() {
        throw new RuntimeException("Should be replaced by mock");
    }

    @GetMapping("/runtime-exception")
    public void testRuntimeException() {
        throw new RuntimeException("Unexpected error");
    }
}
