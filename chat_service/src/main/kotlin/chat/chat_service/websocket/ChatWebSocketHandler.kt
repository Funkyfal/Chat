package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import chat.chat_service.security.jwt.JwtUtil
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ChatWebSocketHandler(
    private val chatKafkaProducer: ChatKafkaProducer,
    private val jwtUtil: JwtUtil
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
            .doOnNext { message ->
                println("От $username получено сообщение: $message")
                chatKafkaProducer.sendMessage("[$username] $message")
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
}