package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import chat.chat_service.redis.RedisChatListener
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
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatWebSocketHandler(
    private val chatKafkaProducer: ChatKafkaProducer,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val redisChatListener: RedisChatListener,
    private val redisMessageListenerContainer: RedisMessageListenerContainer,
    private val redisTemplate: StringRedisTemplate
) : WebSocketHandler {

    companion object {
        private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val query = session.handshakeInfo.uri.query
        val token = extractToken(query)
        val username = token?.let { jwtUtil.validateToken(it) }

        if (username == null) {
            return session.close()
        }
        println("WebSocket: пользователь $username подключился")
        redisChatListener.sessions[username] = session

        val topic = ChannelTopic("chat:$username")
        redisMessageListenerContainer.addMessageListener(redisChatListener, topic)

        return session.receive()
            .map { it.payloadAsText }
            .flatMap { json ->
                val chatMsg = objectMapper.readValue<ChatMessage>(json)

                val kafkaPayload = mapOf(
                    "text" to chatMsg.text,
                    "senderId" to username,
                    "receiverId" to chatMsg.receiverId,
                    "timestamp" to System.currentTimeMillis()
                )
                chatKafkaProducer.sendMessage(objectMapper.writeValueAsString(kafkaPayload))

                val outPayload = mapOf(
                    "text" to chatMsg.text,
                    "senderId" to username,
                    "timestamp" to System.currentTimeMillis()
                )

                val outJson = objectMapper.writeValueAsString(outPayload)
                val channel = "chat:${chatMsg.receiverId}"
                redisTemplate.convertAndSend(channel, outJson)

                Mono.empty<Void>()
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
        val text: String,
        val receiverId: String
    )
}