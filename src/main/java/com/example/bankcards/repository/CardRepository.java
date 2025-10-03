package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByUserId(Long userId);

    List<Card> findByUserIdAndIsActiveTrue(Long userId);

    List<Card> findByCardType(CardType cardType);

    List<Card> findByUserIdAndCardType(Long userId, CardType cardType);

    List<Card> findByBalanceGreaterThan(BigDecimal amount);

    List<Card> findByBalanceBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<Card> findByExpiryDateBefore(LocalDate date);

    @Query("SELECT c FROM Card c WHERE YEAR(c.expiryDate) = :year AND MONTH(c.expiryDate) = :month")
    List<Card> findByExpiryDateYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT c FROM Card c WHERE LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Card> findByCardHolderNameContaining(@Param("name") String name);

    long countByUserId(Long userId);

    long countByUserIdAndIsActiveTrue(Long userId);

    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.isActive = true AND c.balance >= :minBalance")
    List<Card> findActiveCardsForTransfer(@Param("minBalance") BigDecimal minBalance);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    Page<Card> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND " +
           "(LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Card> findByUserIdAndSearchTerm(@Param("userId") Long userId,
                                        @Param("searchTerm") String searchTerm,
                                        Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.isActive = true AND " +
           "(LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Card> findByUserIdAndIsActiveTrueAndSearchTerm(@Param("userId") Long userId,
                                                       @Param("searchTerm") String searchTerm,
                                                       Pageable pageable);

    Page<Card> findAll(Pageable pageable);

    @Query("SELECT c FROM Card c WHERE " +
           "LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Card> findAllBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.username = :username")
    List<Card> findByUserUsername(@Param("username") String username);

    @Query("SELECT c FROM Card c WHERE c.user.username = :username AND c.isActive = true")
    List<Card> findByUserUsernameAndIsActiveTrue(@Param("username") String username);
}
