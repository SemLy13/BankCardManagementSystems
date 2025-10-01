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

    /**
     * Найти карту по номеру
     */
    Optional<Card> findByCardNumber(String cardNumber);

    /**
     * Найти все карты пользователя
     */
    List<Card> findByUserId(Long userId);

    /**
     * Найти активные карты пользователя
     */
    List<Card> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Найти карты по типу
     */
    List<Card> findByCardType(CardType cardType);

    /**
     * Найти карты пользователя по типу
     */
    List<Card> findByUserIdAndCardType(Long userId, CardType cardType);

    /**
     * Найти карты с балансом больше указанного значения
     */
    List<Card> findByBalanceGreaterThan(BigDecimal amount);

    /**
     * Найти карты с балансом в диапазоне
     */
    List<Card> findByBalanceBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Найти карты с истекающим сроком действия до указанной даты
     */
    List<Card> findByExpiryDateBefore(LocalDate date);

    /**
     * Найти карты с истекающим сроком действия в указанном месяце
     */
    @Query("SELECT c FROM Card c WHERE YEAR(c.expiryDate) = :year AND MONTH(c.expiryDate) = :month")
    List<Card> findByExpiryDateYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Найти карты по держателю карты (поиск по имени)
     */
    @Query("SELECT c FROM Card c WHERE LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Card> findByCardHolderNameContaining(@Param("name") String name);

    /**
     * Подсчитать количество карт пользователя
     */
    long countByUserId(Long userId);

    /**
     * Подсчитать количество активных карт пользователя
     */
    long countByUserIdAndIsActiveTrue(Long userId);

    /**
     * Проверить, существует ли карта с данным номером
     */
    boolean existsByCardNumber(String cardNumber);

    /**
     * Найти все активные карты для перевода (исключая заблокированные)
     */
    @Query("SELECT c FROM Card c WHERE c.isActive = true AND c.balance >= :minBalance")
    List<Card> findActiveCardsForTransfer(@Param("minBalance") BigDecimal minBalance);

    /**
     * Найти карты пользователя с пагинацией
     */
    Page<Card> findByUserId(Long userId, Pageable pageable);

    /**
     * Найти активные карты пользователя с пагинацией
     */
    Page<Card> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    /**
     * Поиск карт пользователя по номеру карты или имени держателя с пагинацией
     */
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND " +
           "(LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Card> findByUserIdAndSearchTerm(@Param("userId") Long userId,
                                        @Param("searchTerm") String searchTerm,
                                        Pageable pageable);

    /**
     * Поиск активных карт пользователя по номеру карты или имени держателя с пагинацией
     */
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.isActive = true AND " +
           "(LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Card> findByUserIdAndIsActiveTrueAndSearchTerm(@Param("userId") Long userId,
                                                       @Param("searchTerm") String searchTerm,
                                                       Pageable pageable);

    /**
     * Найти все карты с пагинацией (для администратора)
     */
    Page<Card> findAll(Pageable pageable);

    /**
     * Поиск всех карт по номеру карты или имени держателя с пагинацией (для администратора)
     */
    @Query("SELECT c FROM Card c WHERE " +
           "LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.cardHolderName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Card> findAllBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Найти карты пользователя по имени пользователя
     */
    @Query("SELECT c FROM Card c WHERE c.user.username = :username")
    List<Card> findByUserUsername(@Param("username") String username);

    /**
     * Найти активные карты пользователя по имени пользователя
     */
    @Query("SELECT c FROM Card c WHERE c.user.username = :username AND c.isActive = true")
    List<Card> findByUserUsernameAndIsActiveTrue(@Param("username") String username);
}
