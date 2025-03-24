package chat.message_service.repositories

import chat.message_service.entities.Message
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface MessageRepository : MongoRepository<Message, String>{
    @Query("{ '\$or': [ { 'senderId': ?0, 'receiverId': ?1 }, { 'senderId': ?1, 'receiverId': ?0 } ] }")
    fun findAllByParticipants(senderId: String, receiverId: String): List<Message>
}