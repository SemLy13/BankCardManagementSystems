# Конфигурация

Содержит классы конфигурации Spring Boot: JWT, безопасность, Swagger, CORS и т.п.

## Компоненты конфигурации:

### 1. OpenApiConfig
Конфигурация Swagger/OpenAPI для автоматической генерации документации API.

**Особенности:**
- Настройка схемы безопасности Bearer токенов для JWT аутентификации
- Информация о API (название, описание, версия, контакты)
- Конфигурация сервера разработки
- Глобальное применение аутентификации ко всем защищенным эндпоинтам

**Доступ к документации:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8080/v3/api-docs`

**Настройка безопасности:**
- Схема: `bearerAuth` (HTTP Bearer токен)
- Формат: JWT токен
- Применяется глобально ко всем контроллерам с аннотацией `@SecurityRequirement(name = "bearerAuth")`

**Конфигурация в application.yml:**
```yaml
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
```

### 2. Другие конфигурации (планируемые):
- JWT токены и аутентификация
- Безопасность приложения
- CORS настройки
- Настройки базы данных и миграций
