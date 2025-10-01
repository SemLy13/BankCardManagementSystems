package com.example.bankcards.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.lang.reflect.Field;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для GlobalExceptionHandler
 */
@WebMvcTest
@Import({GlobalExceptionHandler.class, com.example.bankcards.TestConfig.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenIllegalArgumentException_thenReturnsBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Неверные данные запроса"))
                .andExpect(jsonPath("$.error.details").value("Invalid argument"));
    }

    @Test
    void whenResourceNotFoundException_thenReturnsNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("Ресурс не найден"))
                .andExpect(jsonPath("$.error.details").value("Resource not found"));
    }

    @Test
    void whenBusinessException_thenReturnsCustomStatus() throws Exception {
        // when & then
        mockMvc.perform(get("/test/business-exception"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CUSTOM_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Custom business error"));
    }

    @Test
    void whenBadCredentialsException_thenReturnsUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.error.message").value("Ошибка аутентификации"))
                .andExpect(jsonPath("$.error.details").value("Неверное имя пользователя или пароль"));
    }

    @Test
    void whenAccessDeniedException_thenReturnsForbidden() throws Exception {
        // when & then
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.error.message").value("Доступ запрещен"))
                .andExpect(jsonPath("$.error.details").value("У вас недостаточно прав для выполнения этого действия"));
    }

    @Test
    void whenRuntimeException_thenReturnsInternalServerError() throws Exception {
        // when & then
        mockMvc.perform(get("/test/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Внутренняя ошибка сервера"));
    }
}
