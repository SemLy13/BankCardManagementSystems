package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Mapping(target = "fromCardId", source = "fromCard.id")
    @Mapping(target = "toCardId", source = "toCard.id")
    public abstract TransactionDto toDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "fromCard", expression = "java(getCardById(dto.getFromCardId()))")
    @Mapping(target = "toCard", expression = "java(getCardById(dto.getToCardId()))")
    public abstract Transaction toEntity(TransactionDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fromCard", expression = "java(getCardById(dto.getFromCardId()))")
    @Mapping(target = "toCard", expression = "java(getCardById(dto.getToCardId()))")
    public abstract void updateTransactionFromDto(TransactionDto dto, @MappingTarget Transaction transaction);

    protected Card getCardById(Long cardId) {
        // Здесь должен быть вызов сервиса для получения карты
        // Пока оставлю заглушку
        throw new UnsupportedOperationException("Card lookup should be implemented in service layer");
    }
}
