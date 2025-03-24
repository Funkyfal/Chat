package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import chat.chat_service.security.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ChatWebSocketHandler(
    private val chatKafkaProducer: ChatKafkaProducer,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val query = session.handshakeInfo.uri.query
        val token = extractToken(query)
        val username = if (token != null) jwtUtil.validateToken(token) else null

        if (username == null) {
            println("WebSocket: нет валидного токена -> закрываем соединение")
            return session.close()
        }
        println("WebSocket: пользователь $username подключился")

        return session.receive()
            .map { it.payloadAsText }
            .flatMap { json ->
                val chatMsg = objectMapper.readValue<ChatMessage>(json)
                val fullMsg = ChatMessage(
                    text = chatMsg.text,
                    receiverId = chatMsg.receiverId
                )
                val kafkaPayload = mapOf(
                    "text" to fullMsg.text,
                    "senderId" to username,
                    "receiverId" to fullMsg.receiverId,
                    "timestamp" to System.currentTimeMillis()
                )
                chatKafkaProducer.sendMessage(objectMapper.writeValueAsString(kafkaPayload))
                Mono.empty<Void>()
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