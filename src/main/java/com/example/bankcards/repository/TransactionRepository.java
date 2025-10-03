package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


        List<Transaction> findByFromCardId(Long fromCardId);

    List<Transaction> findByToCardId(Long toCardId);


    List<Transaction> findByFromCardIdAndToCardId(Long fromCardId, Long toCardId);


    List<Transaction> findByStatus(TransactionStatus status);


    List<Transaction> findByTransactionType(TransactionType transactionType);


    List<Transaction> findByAmountGreaterThan(BigDecimal amount);


    List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);


    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT t FROM Transaction t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);


    @Query("SELECT t FROM Transaction t WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId ORDER BY t.createdAt DESC")
    List<Transaction> findByCardIdOrderByCreatedAtDesc(@Param("cardId") Long cardId);


    long countByStatus(TransactionStatus status);


    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionType = :type AND t.status = 'COMPLETED'")
    BigDecimal sumAmountByTransactionTypeAndStatusCompleted(@Param("type") TransactionType type);


    @Query(value = "SELECT * FROM transactions WHERE from_card_id = :cardId OR to_card_id = :cardId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Transaction> findTopNByCardIdOrderByCreatedAtDesc(@Param("cardId") Long cardId, @Param("limit") int limit);


    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Transaction> findByDescriptionContaining(@Param("description") String description);


    @Query("SELECT t FROM Transaction t WHERE t.status IN ('FAILED', 'CANCELLED') AND t.createdAt >= :since")
    List<Transaction> findFailedTransactionsSince(@Param("since") LocalDateTime since);
}
