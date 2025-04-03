package chat.chat_service.redis

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.Message
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

class RedisChatListenerTest {

    private val objectMapper = mockk<ObjectMapper>()
    private val listener = RedisChatListener(objectMapper)
    private val session = mockk<WebSocketSession>(relaxed = true)

    @Test
    fun `onMessage should send message if session exists and is open`() {
        val username = "user1"
        val messageContent = "Hello, world!"
        val message = mockk<Message> {
            every { channel } returns "chat:$username".toByteArray()
            every { body } returns messageContent.toByteArray()
        }

        val textMessage = mockk<WebSocketMessage>()

        listener.sessions[username] = session
        every { session.isOpen } returns true
        every { session.textMessage(messageContent) } returns textMessage
        every { session.send(any<Mono<WebSocketMessage>>()) } returns Mono.empty()

        listener.onMessage(message, null)

        verify { session.send(any<Mono<WebSocketMessage>>()) }
    }


    @Test
    fun `onMessage should do nothing if session is closed or doesn't exist`() {
        val username = "user1"
        val message = mockk<Message> {
            every { channel } returns "chat:$username".toByteArray()
            every { body } returns "Hello, world!".toByteArray()
        }

        listener.onMessage(message, null)

        verify(exactly = 0) { session.send(any<Mono<WebSocketMessage>>()) }
    }
}
