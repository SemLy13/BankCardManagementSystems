# Сущности

JPA-сущности для банковского приложения:

## Основные сущности:

### Role
- Управление ролями пользователей
- Поля: id, name, description, createdAt, updatedAt
- Связь: многие-ко-многим с User через user_roles

### User
- Пользователи системы
- Поля: id, username, email, password, firstName, lastName, phoneNumber, enabled, createdAt, updatedAt
- Связи: многие-ко-многим с Role, один-ко-многим с Card

### Card
- Банковские карты пользователей
- Поля: id, userId, cardNumber, cardHolderName, expiryDate, cvv, cardType, balance, isActive, createdAt, updatedAt
- Связи: многие-к-одному с User, один-ко-многим с Transaction (fromCard, toCard)

### Transaction
- Транзакции между картами
- Поля: id, fromCardId, toCardId, amount, currency, transactionType, description, status, createdAt, updatedAt
- Связи: многие-к-одному с Card (fromCard, toCard)

## Enums:
- CardType: DEBIT, CREDIT, PREPAID
- TransactionStatus: PENDING, COMPLETED, FAILED, CANCELLED
- TransactionType: TRANSFER, PAYMENT, DEPOSIT, WITHDRAWAL, REFUND

## Аннотации:
- @Entity, @Table для маппинга таблиц
- @Id, @GeneratedValue для первичных ключей
- @Column для полей с ограничениями
- @ManyToOne, @OneToMany, @ManyToMany для связей
- @CreationTimestamp, @UpdateTimestamp для времени
- @Enumerated для enum полей
