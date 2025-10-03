# Репозитории

Spring Data JPA интерфейсы для доступа к БД.

## UserRepository
- `findByUsername()` - поиск по логину
- `findByEmail()` - поиск по email
- `existsByUsername()` - проверка существования
- `findByEnabledTrue()` - активные пользователи

## RoleRepository
- `findByName()` - поиск роли по имени
- `existsByName()` - проверка существования роли
- `findByNameIn()` - поиск по списку имен

## CardRepository
- `findByCardNumber()` - поиск по номеру карты
- `findByUserId()` - карты пользователя
- `findByCardType()` - карты по типу
- `findByBalanceGreaterThan()` - карты с балансом выше
- `existsByCardNumber()` - проверка существования номера

## TransactionRepository
- `findByFromCardId()` - транзакции отправителя
- `findByToCardId()` - транзакции получателя
- `findByStatus()` - транзакции по статусу
- `findByUserId()` - транзакции пользователя
- `findTopNByCardIdOrderByCreatedAtDesc()` - последние транзакции

## Возможности:
- Наследование от `JpaRepository<Entity, Long>`
- Пагинация, сортировка, кастомные запросы
- Аннотация `@Repository`
