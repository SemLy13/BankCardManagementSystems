package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-03T14:49:46+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class CardMapperImpl extends CardMapper {

    @Override
    public CardDto toDto(Card card) {
        if ( card == null ) {
            return null;
        }

        CardDto cardDto = new CardDto();

        cardDto.setUserId( cardUserId( card ) );
        cardDto.setId( card.getId() );
        cardDto.setCardNumber( card.getCardNumber() );
        cardDto.setCardHolderName( card.getCardHolderName() );
        cardDto.setExpiryDate( card.getExpiryDate() );
        cardDto.setCvv( card.getCvv() );
        cardDto.setCardType( card.getCardType() );
        cardDto.setBalance( card.getBalance() );
        cardDto.setIsActive( card.getIsActive() );
        cardDto.setCreatedAt( card.getCreatedAt() );
        cardDto.setUpdatedAt( card.getUpdatedAt() );

        return cardDto;
    }

    @Override
    public Card toEntity(CardDto cardDto) {
        if ( cardDto == null ) {
            return null;
        }

        Card card = new Card();

        card.setCardNumber( cardDto.getCardNumber() );
        card.setCardHolderName( cardDto.getCardHolderName() );
        card.setExpiryDate( cardDto.getExpiryDate() );
        card.setCvv( cardDto.getCvv() );
        card.setCardType( cardDto.getCardType() );
        card.setBalance( cardDto.getBalance() );
        card.setIsActive( cardDto.getIsActive() );

        card.setUser( getUserById(cardDto.getUserId()) );

        return card;
    }

    @Override
    public void updateCardFromDto(CardDto cardDto, Card card) {
        if ( cardDto == null ) {
            return;
        }

        card.setCardNumber( cardDto.getCardNumber() );
        card.setCardHolderName( cardDto.getCardHolderName() );
        card.setExpiryDate( cardDto.getExpiryDate() );
        card.setCvv( cardDto.getCvv() );
        card.setCardType( cardDto.getCardType() );
        card.setBalance( cardDto.getBalance() );
        card.setIsActive( cardDto.getIsActive() );

        card.setUser( getUserById(cardDto.getUserId()) );
    }

    private Long cardUserId(Card card) {
        if ( card == null ) {
            return null;
        }
        User user = card.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
