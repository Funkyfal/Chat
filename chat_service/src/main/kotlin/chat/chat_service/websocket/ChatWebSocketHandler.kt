package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import chat.chat_service.redis.RedisChatListener
import chat.chat_service.redis.RedisNotificationListener
import chat.chat_service.security.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ChatWebSocketHandler(
    private val chatKafkaProducer: ChatKafkaProducer,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val redisChatListener: RedisChatListener,
    private val redisNotificationListener: RedisNotificationListener,
    private val redisMessageListenerContainer: RedisMessageListenerContainer,
    private val redisTemplate: StringRedisTemplate
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val query = session.handshakeInfo.uri.query
        val token = extractToken(query)
        val username = token?.let { jwtUtil.validateToken(it) }

        if (username == null) {
            return session.close()
        }
        println("WebSocket: пользователь $username подключился")
        redisNotificationListener.sessions[username] = session
        redisChatListener.sessions[username] = session

        val topic = ChannelTopic("chat:$username")
        val notificationTopic = ChannelTopic("notification:$username")
        redisMessageListenerContainer.addMessageListener(redisChatListener, topic)
        redisMessageListenerContainer.addMessageListener(redisNotificationListener, notificationTopic)

        return session.receive()
            .map { it.payloadAsText }
            .flatMap { json ->
                try {
                    val chatMsg = objectMapper.readValue<ChatMessage>(json)
                    when (chatMsg.type) {
                        "setActiveChat" -> {
                            val activeChat = chatMsg.activeChat
                            if (activeChat.isNullOrBlank()) {
                                println("Невозможно переключиться на другой чат, так как он null или пуст")
                                return@flatMap Mono.empty<Void>()
                                TODO("сделать эксепшн")
                            }
                            redisTemplate.opsForValue().set("active_chat:$username", activeChat)
                            println("Установлен новый чат $username вместе с $activeChat")
                            Mono.empty<Void>()
                        }

                        "chatMessage" -> {
                            if (chatMsg.text.isNullOrBlank() || chatMsg.receiverId.isNullOrBlank()) {
                                println("При отправке сообщения от $username text или receiverId оказались пустыми")
                                return@flatMap Mono.empty<Void>()
                                TODO("сделать эксепшн")
                            }
                            val messagePayload = mapOf(
                                "text" to chatMsg.text,
                                "senderId" to username,
                                "receiverId" to chatMsg.receiverId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            chatKafkaProducer.sendMessage(objectMapper.writeValueAsString(messagePayload))

                            val notificationPayload = mapOf(
                                "senderId" to username,
                                "receiverId" to chatMsg.receiverId,
                                "messagePreview" to if (chatMsg.text.length > 20) chatMsg.text.take(20) + "..." else chatMsg.text,
                                "timestamp" to System.currentTimeMillis(),
                                "read" to false
                            )
                            chatKafkaProducer.sendNotification(objectMapper.writeValueAsString(notificationPayload))

                            val receiverActiveChat =
                                redisTemplate.opsForValue().get("active_chat:${chatMsg.receiverId}")
                            if (receiverActiveChat == username) {
                                val outPayload = mapOf(
                                    "text" to chatMsg.text,
                                    "senderId" to username,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                val outJson = objectMapper.writeValueAsString(outPayload)
                                val channel = "chat:${chatMsg.receiverId}"
                                redisTemplate.convertAndSend(channel, outJson)
                                Mono.empty<Void>()
                            } else {
                                println(
                                    "Пользователь ${chatMsg.receiverId} не в чате с $username, отправляем" +
                                            "только уведомление."
                                )
                                Mono.empty<Void>()
                            }
                        }

                        "fileMessage" -> {
                            if(chatMsg.fileUrl.isNullOrBlank() || chatMsg.receiverId.isNullOrBlank()){
                                println("При отправке сообщения от $username fileUrl или receiverId оказались пустыми")
                                return@flatMap Mono.empty<Void>()
                                TODO("сделать эксепшн")
                            }
                            val currentTimestamp = System.currentTimeMillis()

                            val messagePayload = mapOf(
                                "fileUrl" to chatMsg.fileUrl,
                                "text" to chatMsg.text,
                                "senderId" to username,
                                "receiverId" to chatMsg.receiverId,
                                "timestamp" to currentTimestamp
                            )

                            chatKafkaProducer.sendMessage(objectMapper.writeValueAsString(messagePayload))

                            val notificationPayload = mapOf(
                                "senderId" to username,
                                "receiverId" to chatMsg.receiverId,
                                "messagePreview" to "📎Файл",
                                "timestamp" to currentTimestamp,
                                "read" to false
                            )
                            chatKafkaProducer.sendNotification(objectMapper.writeValueAsString(notificationPayload))

                            val receiverActiveChat = redisTemplate.opsForValue().get("active_chat:${chatMsg.receiverId}")
                            if (receiverActiveChat == username) {
                                val outPayload = mapOf(
                                    "fileUrl" to chatMsg.fileUrl,
                                    "text" to chatMsg.text,
                                    "senderId" to username,
                                    "timestamp" to currentTimestamp
                                )
                                val outJson = objectMapper.writeValueAsString(outPayload)
                                val channel = "chat:${chatMsg.receiverId}"
                                redisTemplate.convertAndSend(channel, outJson)
                            } else {
                                println("Пользователь ${chatMsg.receiverId} не в активном чате с $username, уведомление отправлено")
                            }

                            Mono.empty<Void>()
                        }

                        else -> {
                            val errorMsg = "Неверный тип сообщения ${chatMsg.type}"
                            session.send(Mono.just(session.textMessage(errorMsg))).subscribe()
                            Mono.empty<Void>()
                            TODO("сделать эксепшн")
                        }
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                    session.send(
                        Mono.just(
                            session.textMessage("Ошибка обработки сообщения ${e.message}")
                        )
                    ).subscribe()
                    Mono.empty<Void>()
                    TODO("сделать эксепшн")
                }
            }
            .doFinally {
                redisMessageListenerContainer.removeMessageListener(redisChatListener, topic)
                redisChatListener.sessions.remove(username)
                println("WebSocket: пользователь $username отключился")
            }
            .then()
    }

    private fun extractToken(query: String?): String? {
        if (query.isNullOrEmpty()) return null
        val parts = query.split("&")
        for (part in parts) {
            if (part.startsWith("token=")) {
                return part.removePrefix("token=")
            }
        }
        return null
    }

    data class ChatMessage(
        val type: String,
        val text: String? = null,
        val receiverId: String? = null,
        val activeChat: String? = null,
        val fileUrl: String? = null
    )
}