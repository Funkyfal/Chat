package chat.chat_service

import chat.chat_service.kafka.ChatKafkaProducer
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate

class ChatKafkaProducerTest {

    private val kafkaTemplate = mockk<KafkaTemplate<String, String>>(relaxed = true)
    private val chatKafkaProducer = ChatKafkaProducer(kafkaTemplate)

    @Test
    fun `sendMessage should send message to chat-messages topic`() {
        val message = "Test Message"

        chatKafkaProducer.sendMessage(message)

        verify(exactly = 1) { kafkaTemplate.send("chat-messages", message) }
    }

    @Test
    fun `sendNotification should send notification to notification-topic`() {
        val notification = "Test Notification"

        chatKafkaProducer.sendNotification(notification)

        verify(exactly = 1) { kafkaTemplate.send("notification-topic", notification) }
    }
}