package chat.chat_service.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {

    @Bean
    fun chatTopic(): NewTopic {
        return NewTopic("chat-messages", 1, 1.toShort())
    }
}