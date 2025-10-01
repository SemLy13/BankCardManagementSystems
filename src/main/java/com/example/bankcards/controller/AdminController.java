package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageResponse;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST контроллер для административных функций
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "API для административного управления")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    // ==================== CARD MANAGEMENT ====================

    @PostMapping("/cards")
    @Operation(summary = "Создать карту (администратор)", description = "Создает новую банковскую карту")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Карта успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные данные карты"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto cardDto) {
        try {
            Card card = convertCardToEntity(cardDto);
            Card createdCard = cardService.createCard(card);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertCardToDto(createdCard));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/cards/{id}")
    @Operation(summary = "Удалить карту (администратор)", description = "Удаляет карту из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<MessageResponse> deleteCard(@Parameter(description = "ID карты") @PathVariable Long id) {
        try {
            cardService.deleteCard(id);
            return ResponseEntity.ok(new MessageResponse("Карта успешно удалена"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/cards/{id}/block")
    @Operation(summary = "Заблокировать карту (администратор)", description = "Блокирует карту")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<CardDto> blockCard(@Parameter(description = "ID карты") @PathVariable Long id) {
        try {
            Card card = cardService.deactivateCard(id);
            return ResponseEntity.ok(convertCardToDto(card));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/cards/{id}/activate")
    @Operation(summary = "Активировать карту (администратор)", description = "Активирует карту")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Карта успешно активирована"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<CardDto> activateCard(@Parameter(description = "ID карты") @PathVariable Long id) {
        try {
            Card card = cardService.activateCard(id);
            return ResponseEntity.ok(convertCardToDto(card));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cards")
    @Operation(summary = "Получить все карты (администратор)", description = "Возвращает все карты системы с пагинацией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<Page<CardDto>> getAllCards(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Card> cards = cardService.findAllCardsWithPaging(pageable);
        Page<CardDto> cardDtos = cards.map(this::convertCardToDto);
        return ResponseEntity.ok(cardDtos);
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    @Operation(summary = "Получить всех пользователей (администратор)", description = "Возвращает всех пользователей системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> userDtos = users.stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @PostMapping("/users")
    @Operation(summary = "Создать пользователя (администратор)", description = "Создает нового пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
        @ApiResponse(responseCode = "400", description = "Неверные данные пользователя"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest userRequest) {
        try {
            User user = convertUserCreateRequestToEntity(userRequest);
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertUserToDto(createdUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Обновить пользователя (администратор)", description = "Обновляет данные пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<UserDto> updateUser(@Parameter(description = "ID пользователя") @PathVariable Long id,
                                            @Valid @RequestBody UserUpdateRequest userRequest) {
        try {
            User user = convertUserUpdateRequestToEntity(userRequest);
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(convertUserToDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Удалить пользователя (администратор)", description = "Удаляет пользователя из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<MessageResponse> deleteUser(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("Пользователь успешно удален"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private CardDto convertCardToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setUserId(card.getUser().getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setCardHolderName(card.getCardHolderName());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setCvv(card.getCvv());
        dto.setCardType(card.getCardType());
        dto.setBalance(card.getBalance());
        dto.setIsActive(card.getIsActive());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        return dto;
    }

    private Card convertCardToEntity(CardDto dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardHolderName(dto.getCardHolderName());
        card.setExpiryDate(dto.getExpiryDate());
        card.setCvv(dto.getCvv());
        card.setCardType(dto.getCardType());
        card.setBalance(dto.getBalance() != null ? dto.getBalance() : java.math.BigDecimal.ZERO);
        card.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        // Пользователь будет установлен в сервисе через userId
        return card;
    }

    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));
        return dto;
    }

    private User convertUserCreateRequestToEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        return user;
    }

    private User convertUserUpdateRequestToEntity(UserUpdateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        return user;
    }
}
