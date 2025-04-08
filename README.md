
---

# 💬 Chat

Приложение написано на Kotlin с использованием Spring Boot и его модулей, Kafka, Redis, PostgreSQL, MongoDB, MinIO. Предоставляет возможности чата с собеседником через WebSocket соединение: отправка и получение сообщений, получение истории переписки с отдельным пользователем, смена активного чата (перспектива подключения фронтенда), передача файлов (с сохранением в облачном хранилище), отправка уведомлений, если пользователь находится в другом чате или оффлайн.

---
#### Ниже представлено gif-изображение работы приложения:
![API GATEWAY](https://github.com/user-attachments/assets/34668d25-3e8e-4fe0-943c-47661324a1ed) 🎥

---
####  🚀 Приложение состоит из шести микросервисов, каждый из которых выполняет свой функционал. Примеры использования и список эндпоинтов показаны ниже. Коллекция запросов в Postman прикреплена в репозитории. Обратите внимание на то, что реализован Api Gateway Service, который маршрутизирует Ваши запросы. Порты настроены таким образом, что тестирование проходит через 
```
localhost:8085
```

---

## 🔑 Auth-service

### Register

```http
POST /auth/register
```

| Parameter  | Type     | Description                                   |
| :--------- | :------- | :-------------------------------------------- |
| `username` | `string` | **Required**. Имя пользователя. 👤           |
| `password` | `string` | **Required**. Пароль пользователя. 🔒          |

---

### Login

```http
POST /auth/login
```

| Parameter  | Type     | Description                                   |
| :--------- | :------- | :-------------------------------------------- |
| `username` | `string` | **Required**. Имя пользователя. 👤           |
| `password` | `string` | **Required**. Пароль пользователя. 🔒          |

---

## 💌 Chat-service

### Подключение

Для подключения к WebSocket используйте следующий URL:

```http
ws://localhost:8085/ws/chat?token={token}
```

| Parameter | Type     | Description                                        |
| :-------- | :------- | :------------------------------------------------- |
| `token`   | `string` | **Required**. Токен, полученный после логина. 🔑     |

---

### Протокол обмена сообщениями

WebSocket chatWebSocket может обрабатывать различные типы сообщений. Ниже приведены примеры и описание полей каждого типа. 📡

#### 1. Установка активного чата

Это сообщение устанавливает текущий активный чат для пользователя.

**Пример сообщения:**

```json
{
  "type": "setActiveChat",
  "activeChat": "test"
}
```

| Field       | Type     | Description                                                                 |
| :---------- | :------- | :-------------------------------------------------------------------------- |
| `type`      | `string` | Значение должно быть `"setActiveChat"`. 🛠️                                  |
| `activeChat`| `string` | **Required**. Идентификатор или имя чата, который необходимо сделать активным. |

---

#### 2. Текстовое сообщение

Это сообщение отправляет текстовое сообщение другому пользователю.

**Пример сообщения:**

```json
{
  "type": "chatMessage",
  "text": "hello me",
  "receiverId": "test"
}
```

| Field        | Type     | Description                                                            |
| :----------- | :------- | :--------------------------------------------------------------------- |
| `type`       | `string` | Значение должно быть `"chatMessage"`. 💬                                |
| `text`       | `string` | **Required**. Текст отправляемого сообщения. ✉️                         |
| `receiverId` | `string` | **Required**. Идентификатор получателя сообщения. 🎯                   |

---

#### 3. Сообщение с файлом

Это сообщение отправляет файл, доступный по URL, другому пользователю.

**Пример сообщения:**

```json
{
  "type": "fileMessage",
  "fileUrl": "http://minio:9000/chat-files/3ef413cd-96d1-4af2-a277-1e92d4ccc66f-README.md",
  "receiverId": "test"
}
```

| Field        | Type     | Description                                                            |
| :----------- | :------- | :--------------------------------------------------------------------- |
| `type`       | `string` | Значение должно быть `"fileMessage"`. 📎                                 |
| `fileUrl`    | `string` | **Required**. URL файла, который необходимо отправить. 🔗               |
| `text`       | `string` | **Not required**. Текст, который будет прикреплён к файлу. 📝            |
| `receiverId` | `string` | **Required**. Идентификатор получателя сообщения с файлом. 🎯           |

---

## 💬 Message-service

### Get History

```http
GET /message/history?receiverId={receiverId}
```

| Parameter   | Type     | Description                                           |
| :---------- | :------- | :---------------------------------------------------- |
| `receiverId`| `string` | **Required**. Идентификатор получателя сообщений.     |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина. 🔐

---

## 🔔 Notification-service

### Get Notifications

```http
GET /notifications/getNotifications?receiverId={receiverId}
```

| Parameter   | Type     | Description                                           |
| :---------- | :------- | :---------------------------------------------------- |
| `receiverId`| `string` | **Required**. Идентификатор получателя уведомлений.   |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина. 🔐

---

### Mark Notifications as Read

```http
PUT /notifications/markAsRead?senderId={senderId}&receiverId={receiverId}
```

| Parameter    | Type     | Description                                           |
| :----------- | :------- | :---------------------------------------------------- |
| `senderId`   | `string` | **Required**. Идентификатор отправителя уведомлений.  |
| `receiverId` | `string` | **Required**. Идентификатор получателя уведомлений.   |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина. 🔐

---

## 📁 File-storage-service

### Upload File

```http
POST /files/upload
```

| Parameter | Type     | Description                                                   |
| :-------- | :------- | :------------------------------------------------------------ |
| `file`    | `file`   | **Required**. Файл, который необходимо загрузить. 📤         |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина. Формат передачи – multipart/form-data.

---

### Download File

```http
GET /files/{fileName}
```

| Parameter  | Type     | Description                                                           |
| :--------- | :------- | :-------------------------------------------------------------------- |
| `fileName` | `string` | **Required**. Имя файла с идентификатором, например, `93c908e4-a2b1-44b3-90c3-549cbb9a21a4-Lab_5.pdf`. 📥 |

> **Примечание:** Данный эндпоинт требует передачи Bearer-токена в заголовке, полученного после логина. 🔐

---

### 🚀 Инструкция по запуску приложения:

1. **Клонируйте** репозиторий в удобную для вас директорию. 📂
2. В каталоге `Chat` находятся все необходимые файлы, в том числе и `docker-compose.yml`. Запустите терминал в каталоге `Chat` и введите команду:
   ```bash
   docker login -u <username>
   ```
   Если необходимо. Далее введите:
   ```bash
   docker-compose up --build
   ```
   Начнется подгрузка нужных зависимостей и Docker-образов. Ошибок возникнуть не должно (проверялось на нескольких машинах). ✅
3. После запуска приложения импортируйте Postman коллекцию из каталога `Chat` в **Postman**. 📬
4. Для корректной работы приложения сначала необходимо выполнить запросы на регистрацию и логин, после чего ко всем эндпоинтам прикреплять Bearer Token, полученный после успешного логина. Обратите внимание, что в коллекции Postman отсутствуют примеры WebSocket-запросов для Chat Service, поскольку Postman не поддерживает демонстрацию WS соединений. 🌐

---
