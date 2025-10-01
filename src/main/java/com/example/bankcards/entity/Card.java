package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.bankcards.util.CardNumberAttributeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "cards")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number", unique = true, nullable = false, length = 255)
    @Convert(converter = CardNumberAttributeConverter.class)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "cvv", nullable = false, length = 4)
    private String cvv;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType cardType;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CardStatus status = CardStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fromCard", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toCard", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> incomingTransactions = new HashSet<>();

    // Constructors
    public Card() {}

    public Card(User user, String cardNumber, String cardHolderName, LocalDate expiryDate,
                String cvv, CardType cardType) {
        this.user = user;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
    }

    // Вспомогательное: маска номера для отображения
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String plain;
        try {
            // конвертер расшифровывает прозрачно через JPA, но на всякий случай возвращаем уже расшифрованное значение
            plain = this.cardNumber;
        } catch (Exception ex) {
            plain = this.cardNumber;
        }
        String last4 = plain.substring(plain.length() - 4);
        return "**** **** **** " + last4;
    }

    // Вычисление статуса: EXPIRED если истек срок, BLOCKED если не активна
    public CardStatus getCalculatedStatus() {
        if (Boolean.FALSE.equals(isActive)) {
            return CardStatus.BLOCKED;
        }
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            return CardStatus.EXPIRED;
        }
        return CardStatus.ACTIVE;
    }
}
