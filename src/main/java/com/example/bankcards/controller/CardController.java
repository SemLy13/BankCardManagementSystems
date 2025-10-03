package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card Management", description = "API для пользовательских операций с банковскими картами")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить свои карты с пагинацией", description = "Возвращает карты текущего пользователя с поддержкой пагинации и поиска")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры запроса")
    })
    public ResponseEntity<Page<CardDto>> getMyCards(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Поисковый запрос (по номеру карты или имени держателя)") @RequestParam(required = false) String search) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        try {
            Page<Card> cards;
            if (search != null && !search.trim().isEmpty()) {
                cards = cardService.searchActiveUserCardsWithPaging(1L, search, pageable); // Заглушка для userId
            } else {
                cards = cardService.findActiveUserCardsWithPaging(1L, pageable); // Заглушка для userId
            }

            Page<CardDto> cardDtos = cards.map(this::convertToDto);
            return ResponseEntity.ok(cardDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить карту по ID", description = "Возвращает карту по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Карта успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<CardDto> getCardById(@Parameter(description = "ID карты") @PathVariable Long id) {
        Optional<Card> card = cardService.findById(id);
        if (card.isPresent()) {
            return ResponseEntity.ok(convertToDto(card.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<BigDecimal> getCardBalance(@Parameter(description = "ID карты") @PathVariable Long id) {
        Optional<Card> card = cardService.findById(id);
        if (card.isPresent()) {
            return ResponseEntity.ok(card.get().getBalance());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/block-request")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Запросить блокировку карты", description = "Пользователь может запросить блокировку своей карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос на блокировку отправлен"),
        @ApiResponse(responseCode = "400", description = "Карта уже заблокирована или не принадлежит пользователю"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<CardDto> requestCardBlock(@Parameter(description = "ID карты") @PathVariable Long id) {
        try {
            String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            Card card = cardService.requestCardBlock(id, 1L); // Заглушка для userId
            return ResponseEntity.ok(convertToDto(card));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private CardDto convertToDto(Card card) {
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

    private Card convertToEntity(CardDto dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardHolderName(dto.getCardHolderName());
        card.setExpiryDate(dto.getExpiryDate());
        card.setCvv(dto.getCvv());
        card.setCardType(dto.getCardType());
        card.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        card.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        return card;
    }
}
