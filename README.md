# Task Manager

**Task Management System** — это RESTful API для управления задачами с поддержкой ролевой системы, аутентификации и авторизации на основе JWT. Сервис реализован на Java 17+ с использованием Spring Boot, Spring Security, PostgreSQL и Redis.

---

## Функциональность

### Основные возможности:
1. **Аутентификация и авторизация пользователей**:
   - Регистрация и вход через email и пароль.
   - Авторизация через JWT токен.
   - Выход из системы.

2. **Ролевая система**:
   - **Администратор**:
     - Управление задачами (CRUD, изменение статусов, приоритетов, назначение исполнителей).
     - Добавление комментариев.
   - **Пользователь**:
     - Управление задачами, в которых указан как исполнитель (изменение статусов, добавление комментариев).

3. **Управление задачами**:
   - Получение списка задач с фильтрацией по автору, исполнителю, статусу и приоритету.
   - Поддержка пагинации.

4. **Комментарии к задачам**:
   - Просмотр всех комментариев к задаче.
   - Создание и удаление комментариев.

5. **Обработка ошибок**:
   - Валидация входящих данных с понятными сообщениями об ошибках.
   - Возврат ошибок в формате `application/problem+json`.

6. **Документация API**:
   - Swagger UI с описанием всех доступных эндпоинтов.

---

## Используемые технологии

- **Java 17+**
- **Spring Boot** (Core, Web, Security, Data JPA)
- **PostgreSQL** — реляционная база данных.
- **Redis** — хранение JWT токенов.
- **Docker & Docker Compose** — разворачивание dev-среды.
- **Swagger/OpenAPI** — документация API.
- **Lombok** — для упрощения работы с моделями.
- **JUnit** и **Mockito** — для написания базовых тестов.

---

## Установка и запуск

### Требования
- **Docker** и **Docker Compose**
- **JDK 17+**

### Шаги для запуска

1. **Склонируйте репозиторий**:
   ```bash
   git clone https://github.com/SuzdalevAndrey/TaskManager.git
   ```
2. **Перейдите в терминале в папку с файлом docker-compose.yaml**

3. **Запустите контейнеры через Docker Compose**:
   ```bash
   docker-compose up --build
   ```

4. **Swagger UI**:
   После запуска сервиса, Swagger UI будет доступен по адресу:  
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   
---

## Документация API

### Доступные роли
- **ADMIN** — полный доступ к задачам и комментариям.
- **USER** — доступ к задачам, где указан как исполнитель.

### Примеры эндпоинтов
- **POST /api/auth/register** — регистрация пользователя.
- **POST /api/auth/login** — вход в систему.
- **GET /api/tasks** — получение списка задач (доступно админу).
- **PATCH /api/tasks/{id}/status** — изменение статуса задачи.
- **POST /api/tasks/{taskId}/comments** — добавление комментария к задаче.

Полное описание доступно в Swagger UI.

---

## Тестирование

### Запуск тестов
Тесты можно запустить с помощью Maven:
```bash
./mvnw test
```

---

## Особенности реализации

- **JWT в Redis**:
  - Токены хранятся в Redis для обеспечения возможности их отзыва.
