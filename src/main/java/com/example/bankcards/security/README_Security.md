# Безопасность

JWT аутентификация с Spring Security.

## Компоненты:
- **JwtAuthenticationFilter** - обработка JWT токенов
- **UserDetailsServiceImpl** - загрузка пользователей из БД
- **JwtUtil** - генерация/валидация токенов
- **SecurityConfig** - конфигурация доступа

## Доступ:
- `/api/auth/**` - открытые
- `/api/admin/**` - только ADMIN
- `/api/user/**`, `/api/cards/**`, `/api/transactions/**` - USER и ADMIN

## Использование:
```bash
# Логин
POST /api/auth/login
{"username": "admin", "password": "admin123"}
