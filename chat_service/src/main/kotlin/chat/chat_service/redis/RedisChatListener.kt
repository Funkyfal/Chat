package chat.chat_service.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class RedisChatListener(
    private val objectMapper: ObjectMapper
): MessageListener {
    val sessions = ConcurrentHashMap<String, org.springframework.web.reactive.socket.WebSocketSession>()

    override fun onMessage(message: Message, pattern: ByteArray?){
        val channel = message.channel.toString(Charsets.UTF_8)
        val payload = message.body.toString(Charsets.UTF_8)

        val username = channel.removePrefix("chat:")
        val session = sessions[username]
        if (session != null && session.isOpen) {
            val textMessage: WebSocketMessage = session.textMessage(payload)
            session.send(Mono.just(textMessage)).subscribe()
        }
    }
}