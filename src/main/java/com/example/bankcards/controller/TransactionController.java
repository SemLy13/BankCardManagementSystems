package com.example.bankcards.controller;

import com.example.bankcards.dto.MessageResponse;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.TransactionType;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "API для управления банковскими транзакциями")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#fromCardId).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Перевести средства между картами", description = "Выполняет перевод средств между двумя картами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Перевод выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка при переводе (недостаточно средств, неактивные карты и т.д.)"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> transfer(
            @Parameter(description = "ID карты отправителя") @RequestParam Long fromCardId,
            @Parameter(description = "ID карты получателя") @RequestParam Long toCardId,
            @Parameter(description = "Сумма перевода") @RequestParam BigDecimal amount,
            @Parameter(description = "Описание перевода") @RequestParam(required = false) String description) {
        try {
            Transaction transaction = transactionService.createTransferTransaction(fromCardId, toCardId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(transaction));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Перевести средства между своими картами", description = "Выполняет перевод средств между картами пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Перевод выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка при переводе (недостаточно средств, неактивные карты и т.д.)"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> createTransfer(
            @Parameter(description = "ID карты отправителя") @RequestParam Long fromCardId,
            @Parameter(description = "ID карты получателя") @RequestParam Long toCardId,
            @Parameter(description = "Сумма перевода") @RequestParam BigDecimal amount,
            @Parameter(description = "Описание перевода") @RequestParam(required = false) String description) {
        try {
            Transaction transaction = transactionService.createTransferTransaction(fromCardId, toCardId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(transaction));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/payment")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#fromCardId).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Выполнить платеж", description = "Выполняет платеж с карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Платеж выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка при платеже (недостаточно средств, неактивная карта и т.д.)"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> payment(
            @Parameter(description = "ID карты для платежа") @RequestParam Long fromCardId,
            @Parameter(description = "Сумма платежа") @RequestParam BigDecimal amount,
            @Parameter(description = "Описание платежа") @RequestParam(required = false) String description) {
        try {
            Transaction transaction = transactionService.createPaymentTransaction(fromCardId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(transaction));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Подтвердить транзакцию", description = "Подтверждает ожидающую транзакцию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Транзакция подтверждена успешно"),
        @ApiResponse(responseCode = "400", description = "Транзакция не может быть подтверждена"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> confirmTransaction(@Parameter(description = "ID транзакции") @PathVariable Long id) {
        try {
            Transaction transaction = transactionService.confirmTransaction(id);
            return ResponseEntity.ok(convertToDto(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Отменить транзакцию", description = "Отменяет ожидающую транзакцию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Транзакция отменена успешно"),
        @ApiResponse(responseCode = "400", description = "Транзакция не может быть отменена"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> cancelTransaction(@Parameter(description = "ID транзакции") @PathVariable Long id) {
        try {
            Transaction transaction = transactionService.cancelTransaction(id);
            return ResponseEntity.ok(convertToDto(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refund/{originalTransactionId}")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#originalTransactionId).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Выполнить возврат средств", description = "Выполняет возврат средств по выполненной транзакции")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Возврат выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка при возврате (транзакция не найдена, уже отменена и т.д.)"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> refund(
            @Parameter(description = "ID оригинальной транзакции") @PathVariable Long originalTransactionId,
            @Parameter(description = "Причина возврата") @RequestParam(required = false) String reason) {
        try {
            Transaction transaction = transactionService.refundTransaction(originalTransactionId, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(transaction));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().fromCard.user.username == authentication.name or @transactionService.findById(#id).get().toCard.user.username == authentication.name")
    @Operation(summary = "Получить транзакцию по ID", description = "Возвращает транзакцию по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Транзакция успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<TransactionDto> getTransactionById(@Parameter(description = "ID транзакции") @PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.findById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(convertToDto(transaction.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/card/{cardId}")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#cardId).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Получить транзакции по карте", description = "Возвращает все транзакции для указанной карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByCard(@Parameter(description = "ID карты") @PathVariable Long cardId) {
        List<Transaction> transactions = transactionService.findByCardIdOrderedByDate(cardId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/card/{cardId}/recent")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#cardId).get().fromCard.user.username == authentication.name")
    @Operation(summary = "Получить последние транзакции по карте", description = "Возвращает последние N транзакций для указанной карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getRecentTransactionsByCard(
            @Parameter(description = "ID карты") @PathVariable Long cardId,
            @Parameter(description = "Количество транзакций") @RequestParam(defaultValue = "10") int limit) {
        List<Transaction> transactions = transactionService.findLastTransactionsByCard(cardId, limit);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal.id.toString()")
    @Operation(summary = "Получить транзакции пользователя", description = "Возвращает все транзакции для указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByUser(@Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<Transaction> transactions = transactionService.findByUserId(userId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить транзакции по статусу", description = "Возвращает транзакции с указанным статусом")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByStatus(@Parameter(description = "Статус транзакции") @PathVariable TransactionStatus status) {
        List<Transaction> transactions = transactionService.findByStatus(status);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить транзакции по типу", description = "Возвращает транзакции указанного типа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByType(@Parameter(description = "Тип транзакции") @PathVariable TransactionType type) {
        List<Transaction> transactions = transactionService.findByTransactionType(type);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить транзакции за период", description = "Возвращает транзакции за указанный период времени")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByDateRange(
            @Parameter(description = "Дата начала периода") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.findByDateRange(startDate, endDate);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/failed-recent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить неудачные транзакции", description = "Возвращает неудачные транзакции за последние N дней")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список неудачных транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getFailedTransactions(
            @Parameter(description = "Количество дней") @RequestParam(defaultValue = "7") int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Transaction> transactions = transactionService.findFailedTransactionsSince(since);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/statistics/total/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить общую сумму транзакций по типу", description = "Возвращает общую сумму выполненных транзакций указанного типа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сумма успешно получена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<BigDecimal> getTotalAmountByType(@Parameter(description = "Тип транзакции") @PathVariable TransactionType type) {
        BigDecimal total = transactionService.getTotalAmountByTypeAndStatus(type);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/between-cards")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findByCards(#fromCardId, #toCardId).stream().anyMatch(t -> t.fromCard.user.username == authentication.name or t.toCard.user.username == authentication.name)")
    @Operation(summary = "Получить транзакции между картами", description = "Возвращает транзакции между двумя указанными картами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsBetweenCards(
            @Parameter(description = "ID карты отправителя") @RequestParam Long fromCardId,
            @Parameter(description = "ID карты получателя") @RequestParam Long toCardId) {
        List<Transaction> transactions = transactionService.findByCards(fromCardId, toCardId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setFromCardId(transaction.getFromCard().getId());
        dto.setToCardId(transaction.getToCard().getId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setDescription(transaction.getDescription());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        return dto;
    }

    private Transaction convertToEntity(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setDescription(dto.getDescription());
        transaction.setStatus(dto.getStatus());
        return transaction;
    }
}
