package chat.chat_service.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Component
class RedisNotificationListener(
    private val objectMapper: ObjectMapper
): MessageListener {

    val sessions: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()

    override fun onMessage(message: Message, pattern: ByteArray?){
        val channel = message.channel.toString(Charsets.UTF_8)
        val receiver = channel.substringAfter("notification:")
        val session = sessions[receiver]
        if(session != null && session.isOpen){
            val payload = message.body.toString(Charsets.UTF_8)
            session.send(Mono.just(session.textMessage(payload))).subscribe()
            println("Пользователю $receiver отправлено уведомление: $payload")
        }
    }
}