# Сущности

JPA-сущности банковского приложения.

## Основные сущности
- **Role** - роли пользователей (USER, ADMIN)
- **User** - пользователи (username, email, password, роли)
- **Card** - банковские карты (номер, баланс, тип, статус)
- **Transaction** - транзакции (карты, сумма, тип, статус)

## Enums
- **CardType**: DEBIT, CREDIT, PREPAID
- **TransactionStatus**: PENDING, COMPLETED, FAILED, CANCELLED
- **TransactionType**: TRANSFER, PAYMENT, DEPOSIT, WITHDRAWAL, REFUND

## Связи
- User ↔ Role (многие-ко-многим)
- User → Card (один-ко-многим)
- Card → Transaction (один-ко-многим)
