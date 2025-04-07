

## Chat
Приложение написано на Kotlin с использованием Spring Boot и его модулей, Kafka, Redis, PostgreSQL, MongoDB, MinIO. Предоставляет возможности чата с собеседником при помощи WebSocket соединения.

---
#### Ниже представлено gif-изображение работы приложения.
![API GATEWAY.gif](..%2F..%2FDownloads%2FAPI%20GATEWAY.gif)

---
#### Коллекция запросов в Postman прикреплена в репозитории. Также снизу можете увидеть список эндпоинтов.


## Auth-service

### Register

```http
POST /auth/register
```

| Parameter | Type     | Description                                        |
| :-------- | :------- | :------------------------------------------------- |
| `username` | `string` | **Required**. Имя пользователя.                    |
| `password` | `string` | **Required**. Пароль пользователя.               |

---

### Login

```http
POST /auth/login
```

| Parameter | Type     | Description                                        |
| :-------- | :------- | :------------------------------------------------- |
| `username` | `string` | **Required**. Имя пользователя.                    |
| `password` | `string` | **Required**. Пароль пользователя.               |

---

## Chat-service

### Connect

```http
 ws://localhost:8085/ws/chat?token={token}
```

| Parameter | Type     | Description                                        |
| :-------- | :------- | :------------------------------------------------- |
| `token` | `string` | **Required**. Токен, полученный после логина.                    |

---
## Message-service

### Get History

```http
GET /message/history?receiverId={receiverId}
```

| Parameter   | Type     | Description                                           |
| :---------- | :------- | :---------------------------------------------------- |
| `receiverId` | `string` | **Required**. Идентификатор получателя сообщений.     |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина.

---

## Notification-service

### Get Notifications

```http
GET /notifications/getNotifications?receiverId={receiverId}
```

| Parameter   | Type     | Description                                           |
| :---------- | :------- | :---------------------------------------------------- |
| `receiverId` | `string` | **Required**. Идентификатор получателя уведомлений.   |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина.

---

### Mark Notifications as Read

```http
PUT /notifications/markAsRead?senderId={senderId}&receiverId={receiverId}
```

| Parameter    | Type     | Description                                           |
| :----------- | :------- | :---------------------------------------------------- |
| `senderId`   | `string` | **Required**. Идентификатор отправителя уведомлений.  |
| `receiverId` | `string` | **Required**. Идентификатор получателя уведомлений.   |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина.

---

## File-storage-service

### Upload File

```http
POST /files/upload
```

| Parameter | Type     | Description                                                   |
| :-------- | :------- | :------------------------------------------------------------ |
| `file`    | `file`   | **Required**. Файл, который необходимо загрузить.           |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина.
Формат передачи – multipart/form-data.

---

### Download File

```http
GET /files/{fileName}
```

| Parameter  | Type     | Description                                            |
| :--------- | :------- | :----------------------------------------------------- |
| `fileName` | `string` | **Required**. Имя файла с идентификатором, например, `93c908e4-a2b1-44b3-90c3-549cbb9a21a4-Lab_5.pdf`. |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина.

---
### 🚀Инструкция по запуску приложения:
1. Клонируйте репозиторий в удобную для вас директорию.
2. В каталоге `Chat` находятся все необходимые файлы, в том числе и `docker-compose.yml`. Запустите терминал в каталоге `Library` и введите команду ```docker login -u <username>```, если необходимо.
Далее введите ```docker-compose up --build```. Начнется подгрузка нужных зависимостей и докер-образов, ошибок возникнуть не должно. (проверял на трех разных компьютерах, все работало)
3. После запуска приложения импортируйте postman коллекцию из каталога `Chat` в <b>Postman</b>.

