package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import chat.chat_service.security.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap


//need to add Redis
@Component
class ChatWebSocketHandler(
    private val chatKafkaProducer: ChatKafkaProducer,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    companion object {
        private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val query = session.handshakeInfo.uri.query
        val token = extractToken(query)
        val username = token?.let { jwtUtil.validateToken(it) }

        if (username == null) {
            println("WebSocket: нет валидного токена -> закрываем соединение")
            return session.close()
        }

        println("WebSocket: пользователь $username подключился")
        sessions[username] = session

        return session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap { json ->
                val chatMsg: ChatMessage = objectMapper.readValue(json)

                val kafkaPayload = mapOf(
                    "text" to chatMsg.text,
                    "senderId" to username,
                    "receiverId" to chatMsg.receiverId,
                    "timestamp" to System.currentTimeMillis()
                )

                chatKafkaProducer.sendMessage(objectMapper.writeValueAsString(kafkaPayload))

                val receiverSession = sessions[chatMsg.receiverId]
                if (receiverSession != null && receiverSession.isOpen) {
                    val outMsg = mapOf(
                        "text" to chatMsg.text,
                        "senderId" to username,
                        "timestamp" to System.currentTimeMillis()
                    )
                    val outJson = objectMapper.writeValueAsString(outMsg)

                    return@flatMap receiverSession.send(Mono.just(receiverSession.textMessage(outJson)))
                }

                Mono.empty<Void>()
            }
            .doFinally {
                sessions.remove(username)
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