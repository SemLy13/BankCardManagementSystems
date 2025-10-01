# DTO классы

Data Transfer Objects для банковского приложения с валидацией и мапперами.

## Основные DTO:

### UserDto
- Полный DTO для пользователей
- Валидация: username, email, password, firstName, lastName, phoneNumber
- Используется для ответов API

### UserCreateRequest
- DTO для создания пользователя
- Валидация: обязательные username, email, password
- Опциональные: firstName, lastName, phoneNumber

### UserUpdateRequest
- DTO для обновления пользователя
- Все поля опциональные
- Валидация: email формат

### CardDto
- DTO для банковских карт
- Валидация: cardNumber (13-19 цифр), expiryDate (будущая дата), cvv (3-4 цифры)
- Баланс не может быть отрицательным

### TransactionDto
- DTO для транзакций
- Валидация: fromCardId, toCardId, amount (> 0), currency (3 символа)
- Статус по умолчанию PENDING

## Валидация:
- `@NotBlank`, `@NotNull` - обязательные поля
- `@Size` - ограничения длины
- `@Email` - формат email
- `@Pattern` - регулярные выражения
- `@Future` - будущая дата
- `@DecimalMin`, `@Digits` - числовые ограничения

## Мапперы (MapStruct):
- `UserMapper` - преобразование User <-> UserDto
- `CardMapper` - преобразование Card <-> CardDto
- `TransactionMapper` - преобразование Transaction <-> TransactionDto

## Использование:
```java
@Autowired
private UserMapper userMapper;

UserDto userDto = userMapper.toDto(userEntity);
User userEntity = userMapper.toEntity(userCreateRequest);
userMapper.updateUserFromDto(userUpdateRequest, existingUser);
```
