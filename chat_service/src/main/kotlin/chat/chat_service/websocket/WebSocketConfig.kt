package chat.chat_service.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig
    (private val chatWebSocketHandler: ChatWebSocketHandler) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        return SimpleUrlHandlerMapping(mapOf("/ws/chat" to chatWebSocketHandler), 1)
    }

    @Bean
    fun handlerAdapter() = WebSocketHandlerAdapter()
}