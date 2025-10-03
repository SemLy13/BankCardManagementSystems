package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-03T14:49:46+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class TransactionMapperImpl extends TransactionMapper {

    @Override
    public TransactionDto toDto(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setFromCardId( transactionFromCardId( transaction ) );
        transactionDto.setToCardId( transactionToCardId( transaction ) );
        transactionDto.setId( transaction.getId() );
        transactionDto.setAmount( transaction.getAmount() );
        transactionDto.setCurrency( transaction.getCurrency() );
        transactionDto.setTransactionType( transaction.getTransactionType() );
        transactionDto.setDescription( transaction.getDescription() );
        transactionDto.setStatus( transaction.getStatus() );
        transactionDto.setCreatedAt( transaction.getCreatedAt() );
        transactionDto.setUpdatedAt( transaction.getUpdatedAt() );

        return transactionDto;
    }

    @Override
    public Transaction toEntity(TransactionDto dto) {
        if ( dto == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setAmount( dto.getAmount() );
        transaction.setCurrency( dto.getCurrency() );
        transaction.setTransactionType( dto.getTransactionType() );
        transaction.setDescription( dto.getDescription() );

        transaction.setStatus( TransactionStatus.PENDING );
        transaction.setFromCard( getCardById(dto.getFromCardId()) );
        transaction.setToCard( getCardById(dto.getToCardId()) );

        return transaction;
    }

    @Override
    public void updateTransactionFromDto(TransactionDto dto, Transaction transaction) {
        if ( dto == null ) {
            return;
        }

        transaction.setAmount( dto.getAmount() );
        transaction.setCurrency( dto.getCurrency() );
        transaction.setTransactionType( dto.getTransactionType() );
        transaction.setDescription( dto.getDescription() );
        transaction.setStatus( dto.getStatus() );

        transaction.setFromCard( getCardById(dto.getFromCardId()) );
        transaction.setToCard( getCardById(dto.getToCardId()) );
    }

    private Long transactionFromCardId(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }
        Card fromCard = transaction.getFromCard();
        if ( fromCard == null ) {
            return null;
        }
        Long id = fromCard.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long transactionToCardId(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }
        Card toCard = transaction.getToCard();
        if ( toCard == null ) {
            return null;
        }
        Long id = toCard.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
