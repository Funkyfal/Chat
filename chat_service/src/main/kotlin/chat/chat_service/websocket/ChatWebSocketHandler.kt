package chat.chat_service.websocket

import chat.chat_service.kafka.ChatKafkaProducer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ChatWebSocketHandler(private val chatKafkaProducer: ChatKafkaProducer) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.receive()
            .map { it.payloadAsText }
            .doOnNext { message ->
                println("Получено сообщение из WebSocket: $message")
                chatKafkaProducer.sendMessage(message)
            }
            .then()
    }
}