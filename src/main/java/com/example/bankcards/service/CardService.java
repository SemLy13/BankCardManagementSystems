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


@Service
@Transactional(readOnly = true)
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public Card createCard(Card card) {
        if (card.getUser() == null) {
            throw new IllegalArgumentException("Пользователь не установлен в карту");
        }

        System.out.println("CardService: card.getUser() = " + card.getUser());
        System.out.println("CardService: card.getUser().getId() = " + card.getUser().getId());

        if (cardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new IllegalArgumentException("Карта с таким номером уже существует");
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Срок действия карты истек");
        }

        System.out.println("CardService: incoming balance = " + card.getBalance());
        
        if (card.getBalance() == null) {
            System.out.println("CardService: balance is null, setting to ZERO");
            card.setBalance(BigDecimal.ZERO);
        } else {
            System.out.println("CardService: balance is not null, keeping: " + card.getBalance());
        }
        
        if (card.getIsActive() == null) {
            card.setIsActive(true);
        }
        
        System.out.println("CardService: final balance before save = " + card.getBalance());

        return cardRepository.save(card);
    }


    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }


    public Optional<Card> findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }


    public List<Card> findByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }


    public List<Card> findActiveCardsByUserId(Long userId) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId);
    }


    public List<Card> findByCardType(CardType cardType) {
        return cardRepository.findByCardType(cardType);
    }


    public List<Card> findByUserIdAndCardType(Long userId, CardType cardType) {
        return cardRepository.findByUserIdAndCardType(userId, cardType);
    }


    public List<Card> findCardsWithBalanceGreaterThan(BigDecimal amount) {
        return cardRepository.findByBalanceGreaterThan(amount);
    }


    public List<Card> findCardsWithBalanceBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return cardRepository.findByBalanceBetween(minAmount, maxAmount);
    }


    public List<Card> findExpiringCards(LocalDate beforeDate) {
        return cardRepository.findByExpiryDateBefore(beforeDate);
    }


    public List<Card> findByCardHolderName(String name) {
        return cardRepository.findByCardHolderNameContaining(name);
    }


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


    @Transactional
    public Card updateBalance(Long cardId, BigDecimal newBalance) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        card.setBalance(newBalance);
        return cardRepository.save(card);
    }


    @Transactional
    public Card updateCard(Card card) {
        Card existingCard = cardRepository.findById(card.getId())
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!existingCard.getCardNumber().equals(card.getCardNumber()) &&
            cardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new IllegalArgumentException("Карта с таким номером уже существует");
        }

        return cardRepository.save(card);
    }


    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new IllegalArgumentException("Карта не найдена");
        }
        cardRepository.deleteById(id);
    }


    public boolean existsByCardNumber(String cardNumber) {
        return cardRepository.existsByCardNumber(cardNumber);
    }


    public long getUserCardsCount(Long userId) {
        return cardRepository.countByUserId(userId);
    }


    public long getActiveUserCardsCount(Long userId) {
        return cardRepository.countByUserIdAndIsActiveTrue(userId);
    }


    public boolean isCardActiveForTransfer(Long cardId) {
        return cardRepository.findById(cardId)
                .map(card -> card.getIsActive() && card.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .orElse(false);
    }


    public List<Card> findActiveCardsForTransfer(BigDecimal minBalance) {
        return cardRepository.findActiveCardsForTransfer(minBalance);
    }


    public Page<Card> findUserCardsWithPaging(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable);
    }


    public Page<Card> findActiveUserCardsWithPaging(Long userId, Pageable pageable) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId, pageable);
    }


    public Page<Card> searchUserCardsWithPaging(Long userId, String searchTerm, Pageable pageable) {
        return cardRepository.findByUserIdAndSearchTerm(userId, searchTerm, pageable);
    }


    public Page<Card> searchActiveUserCardsWithPaging(Long userId, String searchTerm, Pageable pageable) {
        return cardRepository.findByUserIdAndIsActiveTrueAndSearchTerm(userId, searchTerm, pageable);
    }


    public Page<Card> findAllCardsWithPaging(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }


    public Page<Card> searchAllCardsWithPaging(String searchTerm, Pageable pageable) {
        return cardRepository.findAllBySearchTerm(searchTerm, pageable);
    }


    public List<Card> findUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }


    public List<Card> findActiveUserCards(Long userId) {
        return cardRepository.findByUserIdAndIsActiveTrue(userId);
    }


    public List<Card> findCardsByUsername(String username) {
        return cardRepository.findByUserUsername(username);
    }


    public List<Card> findActiveCardsByUsername(String username) {
        return cardRepository.findByUserUsernameAndIsActiveTrue(username);
    }


    @Transactional
    public Card requestCardBlock(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Карта не принадлежит пользователю");
        }

        if (!card.getIsActive()) {
            throw new IllegalStateException("Карта уже заблокирована");
        }

        card.setIsActive(false);
        card.setStatus(CardStatus.BLOCKED);

        return cardRepository.save(card);
    }


    @Transactional
    public Card unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (card.getIsActive()) {
            throw new IllegalStateException("Карта уже активна");
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Невозможно разблокировать карту с истекшим сроком действия");
        }

        card.setIsActive(true);
        card.setStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }
}
