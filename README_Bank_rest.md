# Система управления банковскими картами

REST API для управления банковскими картами, пользователями и транзакциями с аутентификацией JWT.

## Требования

- **Java**: 17+
- **Maven**: 3.9+
- **Docker**: (опционально для запуска БД)

## Запуск проекта

### 1. Сборка проекта
```bash
# Сборка проекта
.\apache-maven-3.9.6\bin\mvn.cmd clean compile

# Установка зависимостей и сборка
.\apache-maven-3.9.6\bin\mvn.cmd clean install
```

### 2. Запуск базы данных (опционально)
```bash
# Запуск PostgreSQL через Docker Compose
docker-compose up -d postgres

# Или через Docker
docker run -d --name postgres-bank \
  -e POSTGRES_DB=bankcards \
  -e POSTGRES_USER=bank_user \
  -e POSTGRES_PASSWORD=bank_password \
  -p 5432:5432 postgres:15
```

### 3. Запуск приложения
```bash
# Запуск в режиме разработки
.\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

После запуска приложение будет доступно по адресу: http://localhost:8080

### 4. Документация API
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs

## Тестирование

### Запуск всех тестов
```bash
.\apache-maven-3.9.6\bin\mvn.cmd test
```

### Запуск конкретного теста
```bash
.\apache-maven-3.9.6\bin\mvn.cmd test -Dtest=UserRepositoryTest
```

### Запуск с отчетом покрытия
```bash
.\apache-maven-3.9.6\bin\mvn.cmd test jacoco:report
```

## Структура проекта

- `src/main/java` - исходный код приложения
- `src/test/java` - тесты
- `src/main/resources` - конфигурация
- `docker-compose.yml` - настройка контейнеров
- `pom.xml` - зависимости Maven

## Конфигурация

Основные настройки в `application.yml`:
- База данных: PostgreSQL
- Порт сервера: 8080
- JWT секрет для аутентификации
- Настройки логирования
