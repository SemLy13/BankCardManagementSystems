package com.example.bankcards.dto;

import com.example.bankcards.entity.CardType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CardDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Card number must be 13-19 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must not exceed 100 characters")
    private String cardHolderName;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3-4 digits")
    private String cvv;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Balance must have at most 15 integer digits and 2 fractional digits")
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CardDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
