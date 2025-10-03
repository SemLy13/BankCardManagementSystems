# Обработка исключений

Глобальный обработчик ошибок для единого формата ответов API.

## GlobalExceptionHandler
Обрабатывает все исключения и возвращает стандартный формат ошибок.

## Обрабатываемые исключения:
- `IllegalArgumentException` - бизнес-логика ошибки
- `MethodArgumentNotValidException` - ошибки валидации полей
- `BadCredentialsException` - ошибки аутентификации
- `AccessDeniedException` - ошибки авторизации
- `ResourceNotFoundException` - ресурс не найден (404)
- `BusinessException` - пользовательские бизнес-исключения

## Пользовательские исключения:
- **ResourceNotFoundException** - ресурс не найден
- **BusinessException** - бизнес-ошибки с кодом и HTTP статусом

## Формат ответа:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Описание ошибки",
    "timestamp": "2025-10-01 12:00:00",
    "path": "/api/users/123"
  }
}
```
