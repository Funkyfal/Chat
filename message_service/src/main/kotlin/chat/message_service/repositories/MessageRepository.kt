package chat.message_service.repositories

import chat.message_service.entities.Message
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepository : MongoRepository<Message, String>