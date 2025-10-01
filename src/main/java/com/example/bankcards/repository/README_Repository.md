# Репозитории

Интерфейсы Spring Data JPA для доступа к базе данных с дополнительными методами поиска.

## UserRepository
- `findByUsername(String username)` - поиск по логину
- `findByEmail(String email)` - поиск по email
- `existsByUsername(String username)` - проверка существования логина
- `findByEnabledTrue()` - активные пользователи
- `findByNameContaining(String name)` - поиск по имени/фамилии
- `findByRoleName(String roleName)` - пользователи с определенной ролью

## RoleRepository
- `findByName(String name)` - поиск роли по имени
- `existsByName(String name)` - проверка существования роли
- `findByNameIn(Set<String> names)` - поиск ролей по списку имен
- `findByIdInWithUsers(List<Long> roleIds)` - роли с пользователями (оптимизация)

## CardRepository
- `findByCardNumber(String cardNumber)` - поиск карты по номеру
- `findByUserId(Long userId)` - карты пользователя
- `findByCardType(CardType cardType)` - карты по типу
- `findByBalanceGreaterThan(BigDecimal amount)` - карты с балансом выше
- `findByExpiryDateBefore(LocalDate date)` - карты с истекающим сроком
- `countByUserId(Long userId)` - количество карт пользователя
- `existsByCardNumber(String cardNumber)` - проверка существования номера

## TransactionRepository
- `findByFromCardId(Long fromCardId)` - транзакции с карты отправителя
- `findByToCardId(Long toCardId)` - транзакции на карту получателя
- `findByStatus(TransactionStatus status)` - транзакции по статусу
- `findByAmountBetween(BigDecimal min, BigDecimal max)` - транзакции по сумме
- `findByUserId(Long userId)` - транзакции пользователя
- `findTopNByCardIdOrderByCreatedAtDesc(Long cardId, int limit)` - последние транзакции карты
- `sumAmountByTransactionTypeAndStatusCompleted(TransactionType type)` - сумма транзакций по типу

## Общие возможности
- Все репозитории наследуются от `JpaRepository<Entity, Long>`
- Поддерживают пагинацию, сортировку, кастомные запросы
- Аннотированы `@Repository` для компонентного сканирования
