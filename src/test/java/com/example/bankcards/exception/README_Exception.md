# Тесты исключений

Тесты глобального обработчика ошибок.

## Компоненты:
- **GlobalExceptionHandlerTest** - тестирование обработки исключений
- **TestController** - контроллер для генерации тестовых ошибок

## Тестируемые исключения:
- `IllegalArgumentException` (VALIDATION_ERROR)
- `ResourceNotFoundException` (RESOURCE_NOT_FOUND)
- `BusinessException` (пользовательские бизнес-ошибки)
- `BadCredentialsException` (AUTHENTICATION_FAILED)
- `AccessDeniedException` (ACCESS_DENIED)
- `MethodArgumentNotValidException` (VALIDATION_FAILED)

## Формат ответа:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Описание ошибки",
    "timestamp": "2025-10-01T12:00:00"
  }
}
```
