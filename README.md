<h1>🚀 Разработка Системы Управления Банковскими Картами</h1>

<h2>📁 Стартовая структура</h2>
  <p>
    Проектная структура с директориями и описательными файлами (<code>README Controller.md</code>, <code>README Service.md</code> и т.д.) уже подготовлена.<br />
    Все реализации нужно добавлять <strong>в соответствующие директории</strong>.
  </p>
  <p>
    После завершения разработки <strong>временные README-файлы нужно удалить</strong>, чтобы они не попадали в итоговую сборку.
  </p>
  
<h2>📝 Описание задачи</h2>
  <p>Разработать backend-приложение на Java (Spring Boot) для управления банковскими картами:</p>
  <ul>
    <li>Создание и управление картами</li>
    <li>Просмотр карт</li>
    <li>Переводы между своими картами</li>
  </ul>

<h2>💳 Атрибуты карты</h2>
  <ul>
    <li>Номер карты (зашифрован, отображается маской: <code>**** **** **** 1234</code>)</li>
    <li>Владелец</li>
    <li>Срок действия</li>
    <li>Статус: Активна, Заблокирована, Истек срок</li>
    <li>Баланс</li>
  </ul>

<h2>🧾 Требования</h2>

<h3>✅ Аутентификация и авторизация</h3>
  <ul>
    <li>Spring Security + JWT</li>
    <li>Роли: <code>ADMIN</code> и <code>USER</code></li>
  </ul>

<h3>✅ Возможности</h3>
<strong>Администратор:</strong>
  <ul>
    <li>Создаёт, блокирует, активирует, удаляет карты</li>
    <li>Управляет пользователями</li>
    <li>Видит все карты</li>
  </ul>

<strong>Пользователь:</strong>
  <ul>
    <li>Просматривает свои карты (поиск + пагинация)</li>
    <li>Запрашивает блокировку карты</li>
    <li>Делает переводы между своими картами</li>
    <li>Смотрит баланс</li>
  </ul>

<h3>✅ API</h3>
  <ul>
    <li>CRUD для карт</li>
    <li>Переводы между своими картами</li>
    <li>Фильтрация и постраничная выдача</li>
    <li>Валидация и сообщения об ошибках</li>
  </ul>

<h3>✅ Безопасность</h3>
  <ul>
    <li>Шифрование данных</li>
    <li>Ролевой доступ</li>
    <li>Маскирование номеров карт</li>
  </ul>

<h3>✅ Работа с БД</h3>
  <ul>
    <li>PostgreSQL или MySQL</li>
    <li>Миграции через Liquibase (<code>src/main/resources/db/migration</code>)</li>
  </ul>

<h3>✅ Документация</h3>
  <ul>
    <li>Swagger UI / OpenAPI — <code>docs/openapi.yaml</code></li>
    <li>Интерактивная документация API через Swagger UI</li>
    <li><code>README.md</code> с инструкцией запуска</li>
  </ul>

<h3>📖 Документация API</h3>
<p>После запуска приложения доступна интерактивная документация:</p>
<ul>
  <li><strong>Swagger UI:</strong> <code>http://localhost:8080/swagger-ui.html</code></li>
  <li><strong>OpenAPI спецификация:</strong> <code>http://localhost:8080/v3/api-docs</code></li>
</ul>
<p>Документация включает:</p>
<ul>
  <li>Все доступные эндпоинты с описаниями</li>
  <li>Модели данных (DTO)</li>
  <li>Схемы безопасности (Bearer токен JWT)</li>
  <li>Возможность тестирования API прямо в браузере</li>
  <li>Примеры запросов и ответов</li>
</ul>

<h3>✅ Развёртывание и тестирование</h3>
  <ul>
    <li>Docker Compose для dev-среды</li>
    <li>Liquibase миграции</li>
    <li>Юнит-тесты ключевой бизнес-логики</li>
  </ul>

<h2>🚀 Запуск и тестирование</h2>

<h3>📋 Предварительные требования</h3>
<ul>
  <li>Java 17+</li>
  <li>Maven 3.6+</li>
  <li>PostgreSQL (или Docker для запуска через docker-compose)</li>
</ul>

<h3>🏃 Запуск приложения</h3>

<h4>Способ 1: Через Docker Compose (рекомендуется)</h4>
<pre><code>docker-compose up -d</code></pre>
<p>Это запустит:</p>
<ul>
  <li>PostgreSQL базу данных</li>
  <li>Приложение Spring Boot</li>
  <li>Liquibase миграции автоматически</li>
</ul>

<h4>Способ 2: Локальный запуск</h4>
<ol>
  <li>Настройте PostgreSQL локально</li>
  <li>Обновите настройки в <code>application.yml</code></li>
  <li>Выполните: <code>mvn spring-boot:run</code></li>
</ol>

<h3>🔐 Аутентификация</h3>
<p>Для доступа к защищенным эндпоинтам используйте JWT токены:</p>
<ol>
  <li>Авторизуйтесь через <code>POST /api/auth/login</code></li>
  <li>Получите токен в ответе</li>
  <li>Используйте токен в заголовке <code>Authorization: Bearer &lt;token&gt;</code></li>
</ol>

<h3>🧪 Тестирование API</h3>

<h4>Через Swagger UI</h4>
<ol>
  <li>Откройте <code>http://localhost:8080/swagger-ui.html</code></li>
  <li>Нажмите "Authorize" и введите JWT токен</li>
  <li>Тестируйте эндпоинты прямо в интерфейсе</li>
</ol>

<h4>Через curl/Postman</h4>
<pre><code># Пример авторизации
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Пример получения пользователей (требует токен)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer &lt;ваш_jwt_токен&gt;"
</code></pre>

<h2>📊 Оценка</h2>
  <ul>
    <li>Соответствие требованиям</li>
    <li>Чистота архитектуры и кода</li>
    <li>Безопасность</li>
    <li>Обработка ошибок</li>
    <li>Покрытие тестами</li>
    <li>ООП и уровни абстракции</li>
  </ul>

<h2>💡 Технологии</h2>
  <p>
    Java 17+, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL/MySQL, Liquibase, Docker, JWT, Swagger (OpenAPI)
  </p>

<h2> 📤 Формат сдачи</h2>
<p>
Весь код и изменения принимаются только через git-репозиторий с открытым доступом к проекту. Отправка файлов в любом виде не принимается.
  </p>
