# Настройки безопасности

Spring Security с JWT аутентификацией настроена для банковского приложения.

## Компоненты безопасности:

### JwtAuthenticationFilter
- Фильтр для обработки JWT токенов в заголовках Authorization
- Извлекает токен из Bearer заголовка
- Валидирует токен и устанавливает аутентификацию в SecurityContext

### UserDetailsServiceImpl
- Загружает пользователей из базы данных
- Преобразует роли пользователя в GrantedAuthority
- Используется для аутентификации

### JwtUtil
- Утилита для работы с JWT токенами
- Генерация токенов для аутентифицированных пользователей
- Валидация токенов и извлечение username

### SecurityConfig
- Основная конфигурация Spring Security
- Бин PasswordEncoder (BCrypt)
- Бин AuthenticationManager
- Правила доступа к эндпоинтам:
  - `/api/auth/**` - открытые (логин/логаут)
  - `/swagger-ui/**`, `/v3/api-docs/**` - открытые (документация)
  - `/actuator/**` - открытые (мониторинг)
  - `/api/admin/**` - только ADMIN
  - `/api/user/**`, `/api/cards/**`, `/api/transactions/**` - USER и ADMIN

## Использование:

### Аутентификация:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Ответ:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

### Использование токена:
```bash
GET /api/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Конфигурация:
- JWT секрет и время жизни в `application.yml`
- Пароли хэшируются BCrypt
- Stateless сессии (без cookies)
