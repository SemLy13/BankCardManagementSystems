# Тесты

Комплексное тестирование банковской системы.

## Структура тестов:

### Репозитории (@DataJpaTest)
- **UserRepositoryTest** - тестирование БД операций
- Проверка кастомных запросов и CRUD операций

### Сервисы (@ExtendWith(MockitoExtension.class))
- **UserServiceTest** - тестирование бизнес-логики
- Мокирование зависимостей

### Контроллеры
- **UserControllerTest** - REST API тесты
- **AuthControllerTest** - аутентификация
- **UserManagementIntegrationTest** - интеграционные тесты

### Обработка исключений
- **GlobalExceptionHandlerTest** - тестирование ошибок

## Покрываемые сценарии:
- CRUD операции
- Аутентификация и авторизация
- Ролевая модель доступа
- Валидация данных
- JWT токены и безопасность

## Запуск:
```bash
# Все тесты
mvn test

# Конкретный класс
mvn test -Dtest=UserRepositoryTest

# С покрытием
mvn test jacoco:report
```
```
# или перейти в корень проекта и запустить
.\apache-maven-3.9.6\bin\mvn.cmd test
```
