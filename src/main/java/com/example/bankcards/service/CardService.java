package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardType;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления банковскими картами
 * Содержит бизнес-логику и транзакционные операции
 */
@Service
@Transactional(readOnly = true)
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Создать новую карту для пользователя
     */
    @Transactional
    public Card createCard(Card card) {
        // Проверка существования пользователя
        User user = userRepository.findById(card.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверка уникальности номера карты
        if (cardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new IllegalArgumentException("Карта с таким номером уже существует");
        }

        // Проверка срока действия карты
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Срок действия карты истек");
        }

        card.setUser(user);
        card.setBalance(BigDecimal.ZERO);
        card.setIsActive(true);

        return cardRepository.save(card);
    }

    /**
     * Найти карту по ID
     */
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    /**
     * Найти карту по номеру
     */
    public Optional<Card> findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    /**
     * Получить все карты пользователя
     */
    public List<Card> findByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    /**
     * Получить активные карты пользователя
     */
    public List<Card> findActiveCardsByUserId(Long userId) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Найти карты по типу
     */
    public List<Card> findByCardType(CardType cardType) {
        return cardRepository.findByCardType(cardType);
    }

    /**
     * Найти карты пользователя по типу
     */
    public List<Card> findByUserIdAndCardType(Long userId, CardType cardType) {
        return cardRepository.findByUserIdAndCardType(userId, cardType);
    }

    /**
     * Найти карты с балансом больше указанного значения
     */
    public List<Card> findCardsWithBalanceGreaterThan(BigDecimal amount) {
        return cardRepository.findByBalanceGreaterThan(amount);
    }

    /**
     * Найти карты с балансом в диапазоне
     */
    public List<Card> findCardsWithBalanceBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return cardRepository.findByBalanceBetween(minAmount, maxAmount);
    }

    /**
     * Найти карты с истекающим сроком действия
     */
    public List<Card> findExpiringCards(LocalDate beforeDate) {
        return cardRepository.findByExpiryDateBefore(beforeDate);
    }

    /**
     * Найти карты по имени держателя
     */
    public List<Card> findByCardHolderName(String name) {
        return cardRepository.findByCardHolderNameContaining(name);
    }

    /**
     * Активировать карту
     */
    @Transactional
    public Card activateCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (card.getIsActive()) {
            throw new IllegalStateException("Карта уже активна");
        }

        card.setIsActive(true);
        return cardRepository.save(card);
    }

    /**
     * Деактивировать карту
     */
    @Transactional
    public Card deactivateCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getIsActive()) {
            throw new IllegalStateException("Карта уже деактивирована");
        }

        card.setIsActive(false);
        return cardRepository.save(card);
    }

    /**
     * Пополнить баланс карты
     */
    @Transactional
    public Card deposit(Long cardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getIsActive()) {
            throw new IllegalStateException("Карта не активна");
        }

        card.setBalance(card.getBalance().add(amount));
        return cardRepository.save(card);
    }

    /**
     * Списать средства с карты
     */
    @Transactional
    public Card withdraw(Long cardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getIsActive()) {
            throw new IllegalStateException("Карта не активна");
        }

        if (card.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на карте");
        }

        card.setBalance(card.getBalance().subtract(amount));
        return cardRepository.save(card);
    }

    /**
     * Обновить баланс карты (для внутренних операций)
     */
    @Transactional
    public Card updateBalance(Long cardId, BigDecimal newBalance) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        card.setBalance(newBalance);
        return cardRepository.save(card);
    }

    /**
     * Обновить информацию о карте
     */
    @Transactional
    public Card updateCard(Card card) {
        Card existingCard = cardRepository.findById(card.getId())
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        // Проверка уникальности номера карты
        if (!existingCard.getCardNumber().equals(card.getCardNumber()) &&
            cardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new IllegalArgumentException("Карта с таким номером уже существует");
        }

        return cardRepository.save(card);
    }

    /**
     * Удалить карту
     */
    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new IllegalArgumentException("Карта не найдена");
        }
        cardRepository.deleteById(id);
    }

    /**
     * Проверить, существует ли карта с данным номером
     */
    public boolean existsByCardNumber(String cardNumber) {
        return cardRepository.existsByCardNumber(cardNumber);
    }

    /**
     * Получить количество карт пользователя
     */
    public long getUserCardsCount(Long userId) {
        return cardRepository.countByUserId(userId);
    }

    /**
     * Получить количество активных карт пользователя
     */
    public long getActiveUserCardsCount(Long userId) {
        return cardRepository.countByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Проверить, активна ли карта для переводов
     */
    public boolean isCardActiveForTransfer(Long cardId) {
        return cardRepository.findById(cardId)
                .map(card -> card.getIsActive() && card.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .orElse(false);
    }

    /**
     * Найти активные карты для перевода с минимальным балансом
     */
    public List<Card> findActiveCardsForTransfer(BigDecimal minBalance) {
        return cardRepository.findActiveCardsForTransfer(minBalance);
    }

    /**
     * Найти карты пользователя с пагинацией
     */
    public Page<Card> findUserCardsWithPaging(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable);
    }

    /**
     * Найти активные карты пользователя с пагинацией
     */
    public Page<Card> findActiveUserCardsWithPaging(Long userId, Pageable pageable) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId, pageable);
    }

    /**
     * Поиск карт пользователя по номеру карты или имени держателя с пагинацией
     */
    public Page<Card> searchUserCardsWithPaging(Long userId, String searchTerm, Pageable pageable) {
        return cardRepository.findByUserIdAndSearchTerm(userId, searchTerm, pageable);
    }

    /**
     * Поиск активных карт пользователя по номеру карты или имени держателя с пагинацией
     */
    public Page<Card> searchActiveUserCardsWithPaging(Long userId, String searchTerm, Pageable pageable) {
        return cardRepository.findByUserIdAndIsActiveTrueAndSearchTerm(userId, searchTerm, pageable);
    }

    /**
     * Найти все карты с пагинацией (для администратора)
     */
    public Page<Card> findAllCardsWithPaging(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    /**
     * Поиск всех карт по номеру карты или имени держателя с пагинацией (для администратора)
     */
    public Page<Card> searchAllCardsWithPaging(String searchTerm, Pageable pageable) {
        return cardRepository.findAllBySearchTerm(searchTerm, pageable);
    }

    /**
     * Получить все карты пользователя (для совместимости)
     */
    public List<Card> findUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    /**
     * Получить все активные карты пользователя (для совместимости)
     */
    public List<Card> findActiveUserCards(Long userId) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Найти карты пользователя по имени пользователя (для администратора)
     */
    public List<Card> findCardsByUsername(String username) {
        return cardRepository.findByUserUsername(username);
    }

    /**
     * Найти активные карты пользователя по имени пользователя (для администратора)
     */
    public List<Card> findActiveCardsByUsername(String username) {
        return cardRepository.findByUserUsernameAndIsActiveTrue(username);
    }

    /**
     * Запрос блокировки карты пользователем
     * Пользователь может запросить блокировку своей карты
     */
    @Transactional
    public Card requestCardBlock(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        // Проверка, что карта принадлежит пользователю
        if (!card.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Карта не принадлежит пользователю");
        }

        // Проверка, что карта активна
        if (!card.getIsActive()) {
            throw new IllegalStateException("Карта уже заблокирована");
        }

        // Устанавливаем карту как неактивную (блокировка)
        card.setIsActive(false);
        card.setStatus(CardStatus.BLOCKED);

        return cardRepository.save(card);
    }

    /**
     * Разблокировать карту (только администратор)
     */
    @Transactional
    public Card unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        // Проверка, что карта заблокирована
        if (card.getIsActive()) {
            throw new IllegalStateException("Карта уже активна");
        }

        // Проверка, что срок действия карты не истек
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Невозможно разблокировать карту с истекшим сроком действия");
        }

        card.setIsActive(true);
        card.setStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }
}
