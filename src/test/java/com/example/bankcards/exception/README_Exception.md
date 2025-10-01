# Тесты исключений

Тесты для глобального обработчика исключений (GlobalExceptionHandler).

## Созданные тесты:

### 1. GlobalExceptionHandlerTest (@WebMvcTest)
- Тестирование обработки различных типов исключений
- Проверка корректного форматирования ответов с ошибками
- Тестирование HTTP статус-кодов
- Проверка детальной информации об ошибках валидации

### 2. TestController
- Вспомогательный контроллер для генерации исключений в тестах
- Предоставляет эндпоинты для тестирования различных сценариев ошибок

## Тестируемые типы исключений:
- ✅ IllegalArgumentException (VALIDATION_ERROR)
- ✅ ResourceNotFoundException (RESOURCE_NOT_FOUND)
- ✅ BusinessException (пользовательские бизнес-ошибки)
- ✅ BadCredentialsException (AUTHENTICATION_FAILED)
- ✅ AccessDeniedException (ACCESS_DENIED)
- ✅ MethodArgumentNotValidException (VALIDATION_FAILED)
- ✅ RuntimeException (INTERNAL_ERROR)

## Формат ответа с ошибкой:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Описание ошибки",
    "details": "Дополнительная информация",
    "timestamp": "2025-10-01T12:00:00",
    "path": "/api/users/123",
    "validationErrors": {
      "field1": "Сообщение об ошибке поля 1"
    }
  }
}
```
