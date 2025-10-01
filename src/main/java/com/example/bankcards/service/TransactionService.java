package com.example.bankcards.service;

import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления банковскими транзакциями
 * Содержит бизнес-логику переводов и управления транзакциями
 */
@Service
@Transactional(readOnly = true)
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    /**
     * Создать транзакцию перевода между картами
     */
    @Transactional
    public Transaction createTransferTransaction(Long fromCardId, Long toCardId,
                                                BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        if (fromCardId.equals(toCardId)) {
            throw new IllegalArgumentException("Нельзя переводить средства на ту же карту");
        }

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта отправителя не найдена"));

        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта получателя не найдена"));

        // Проверка активности карт
        if (!fromCard.getIsActive()) {
            throw new IllegalStateException("Карта отправителя не активна");
        }

        if (!toCard.getIsActive()) {
            throw new IllegalStateException("Карта получателя не активна");
        }

        // Проверка достаточности средств
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на карте отправителя");
        }

        // Создание транзакции
        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setDescription(description != null ? description : "Перевод между картами");
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Выполнение перевода
        try {
            executeTransfer(savedTransaction);
        } catch (Exception e) {
            savedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);
            throw new RuntimeException("Ошибка при выполнении перевода: " + e.getMessage());
        }

        return savedTransaction;
    }

    /**
     * Выполнить перевод средств (внутренний метод)
     */
    @Transactional
    public void executeTransfer(Transaction transaction) {
        Card fromCard = transaction.getFromCard();
        Card toCard = transaction.getToCard();
        BigDecimal amount = transaction.getAmount();

        // Списание средств с карты отправителя
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        cardRepository.save(fromCard);

        // Пополнение карты получателя
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(toCard);

        // Обновление статуса транзакции
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    /**
     * Создать транзакцию платежа
     */
    @Transactional
    public Transaction createPaymentTransaction(Long fromCardId, BigDecimal amount,
                                              String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма платежа должна быть положительной");
        }

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!fromCard.getIsActive()) {
            throw new IllegalStateException("Карта не активна");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на карте");
        }

        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.PAYMENT);
        transaction.setDescription(description != null ? description : "Платеж");
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Выполнение платежа
        try {
            executePayment(savedTransaction);
        } catch (Exception e) {
            savedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);
            throw new RuntimeException("Ошибка при выполнении платежа: " + e.getMessage());
        }

        return savedTransaction;
    }

    /**
     * Выполнить платеж (внутренний метод)
     */
    @Transactional
    public void executePayment(Transaction transaction) {
        Card fromCard = transaction.getFromCard();
        BigDecimal amount = transaction.getAmount();

        // Списание средств
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        cardRepository.save(fromCard);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    /**
     * Подтвердить транзакцию
     */
    @Transactional
    public Transaction confirmTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Транзакция не найдена"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Транзакция не может быть подтверждена в текущем статусе");
        }

        switch (transaction.getTransactionType()) {
            case TRANSFER:
                executeTransfer(transaction);
                break;
            case PAYMENT:
                executePayment(transaction);
                break;
            case DEPOSIT:
                executeDeposit(transaction);
                break;
            case WITHDRAWAL:
                executeWithdrawal(transaction);
                break;
            default:
                throw new IllegalStateException("Неподдерживаемый тип транзакции");
        }

        return transaction;
    }

    /**
     * Отменить транзакцию
     */
    @Transactional
    public Transaction cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Транзакция не найдена"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Только ожидающие транзакции могут быть отменены");
        }

        transaction.setStatus(TransactionStatus.CANCELLED);
        return transactionRepository.save(transaction);
    }

    /**
     * Выполнить возврат средств
     */
    @Transactional
    public Transaction refundTransaction(Long originalTransactionId, String reason) {
        Transaction originalTransaction = transactionRepository.findById(originalTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Исходная транзакция не найдена"));

        if (originalTransaction.getTransactionType() != TransactionType.TRANSFER &&
            originalTransaction.getTransactionType() != TransactionType.PAYMENT) {
            throw new IllegalArgumentException("Возврат возможен только для переводов и платежей");
        }

        if (originalTransaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Возврат возможен только для выполненных транзакций");
        }

        Card fromCard = originalTransaction.getToCard();
        Card toCard = originalTransaction.getFromCard();
        BigDecimal amount = originalTransaction.getAmount();

        // Создание транзакции возврата
        Transaction refundTransaction = new Transaction();
        refundTransaction.setFromCard(fromCard);
        refundTransaction.setToCard(toCard);
        refundTransaction.setAmount(amount);
        refundTransaction.setTransactionType(TransactionType.REFUND);
        refundTransaction.setDescription("Возврат: " + (reason != null ? reason : "Без причины"));
        refundTransaction.setStatus(TransactionStatus.PENDING);

        Transaction savedRefund = transactionRepository.save(refundTransaction);

        try {
            executeTransfer(savedRefund);
        } catch (Exception e) {
            savedRefund.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedRefund);
            throw new RuntimeException("Ошибка при выполнении возврата: " + e.getMessage());
        }

        return savedRefund;
    }

    /**
     * Найти транзакцию по ID
     */
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Получить транзакции по карте отправителя
     */
    public List<Transaction> findByFromCardId(Long fromCardId) {
        return transactionRepository.findByFromCardId(fromCardId);
    }

    /**
     * Получить транзакции по карте получателя
     */
    public List<Transaction> findByToCardId(Long toCardId) {
        return transactionRepository.findByToCardId(toCardId);
    }

    /**
     * Получить транзакции между двумя картами
     */
    public List<Transaction> findByCards(Long fromCardId, Long toCardId) {
        return transactionRepository.findByFromCardIdAndToCardId(fromCardId, toCardId);
    }

    /**
     * Получить транзакции пользователя
     */
    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Получить транзакции по статусу
     */
    public List<Transaction> findByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    /**
     * Получить транзакции по типу
     */
    public List<Transaction> findByTransactionType(TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }

    /**
     * Получить транзакции за период
     */
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Получить транзакции по карте с пагинацией
     */
    public List<Transaction> findByCardIdOrderedByDate(Long cardId) {
        return transactionRepository.findByCardIdOrderByCreatedAtDesc(cardId);
    }

    /**
     * Получить последние N транзакций по карте
     */
    public List<Transaction> findLastTransactionsByCard(Long cardId, int limit) {
        return transactionRepository.findTopNByCardIdOrderByCreatedAtDesc(cardId, limit);
    }

    /**
     * Получить сумму транзакций по типу и статусу
     */
    public BigDecimal getTotalAmountByTypeAndStatus(TransactionType type) {
        return transactionRepository.sumAmountByTransactionTypeAndStatusCompleted(type);
    }

    /**
     * Найти неудачные транзакции за период
     */
    public List<Transaction> findFailedTransactionsSince(LocalDateTime since) {
        return transactionRepository.findFailedTransactionsSince(since);
    }

    // Внутренние методы для выполнения операций

    @Transactional
    private void executeDeposit(Transaction transaction) {
        Card toCard = transaction.getToCard();
        BigDecimal amount = transaction.getAmount();

        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(toCard);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    @Transactional
    private void executeWithdrawal(Transaction transaction) {
        Card fromCard = transaction.getFromCard();
        BigDecimal amount = transaction.getAmount();

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств для снятия");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        cardRepository.save(fromCard);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }
}
