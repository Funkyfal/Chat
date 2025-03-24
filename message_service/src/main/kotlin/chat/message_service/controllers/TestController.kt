package chat.message_service.controllers

import chat.message_service.entities.Message
import chat.message_service.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message")
class TestController(@Autowired private val messageRepository: MessageRepository) {

    @GetMapping("/history")
    fun getHistory(@RequestParam senderId: String, @RequestParam receiverId: String): List<Message>{
        return messageRepository.findAllByParticipants(senderId, receiverId)
    }
}