# DTO классы

Data Transfer Objects с валидацией.

## Основные DTO
- **UserDto** - пользователи (username, email, password)
- **UserCreateRequest** - создание пользователя
- **UserUpdateRequest** - обновление пользователя
- **CardDto** - банковские карты (номер, срок, CVV, баланс)
- **TransactionDto** - транзакции (карты, сумма, статус)

## Валидация
- `@NotBlank`, `@NotNull` - обязательные поля
- `@Email`, `@Pattern` - формат данных
- `@Size`, `@DecimalMin` - ограничения

## Мапперы (MapStruct)
- `UserMapper` - User ↔ UserDto
- `CardMapper` - Card ↔ CardDto
- `TransactionMapper` - Transaction ↔ TransactionDto
