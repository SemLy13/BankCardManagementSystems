# Контроллеры

REST API для банковской системы.

## AuthController (`/api/auth`)
- POST `/login` - аутентификация
- POST `/logout` - выход

## UserController (`/api/users`)
- GET `/` - все пользователи (ADMIN)
- GET `/{id}` - пользователь по ID
- POST `/` - создать пользователя (ADMIN)
- PUT `/{id}` - обновить пользователя
- DELETE `/{id}` - удалить пользователя (ADMIN)

## CardController (`/api/cards`)
- GET `/{id}` - карта по ID
- POST `/` - создать карту
- PUT `/{id}` - обновить карту
- GET `/{id}/balance` - баланс карты
- POST `/{id}/block-request` - запрос блокировки

## TransactionController (`/api/transactions`)
- POST `/` - перевод между картами
- POST `/payment` - платеж
- GET `/{id}` - транзакция по ID
- GET `/card/{cardId}` - транзакции по карте

## Особенности
- JWT аутентификация
- Ролевая авторизация (USER/ADMIN)
- Swagger документация
- Валидация данных
