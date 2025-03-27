package chat.message_service.controllers

import chat.message_service.services.MessageService
import chat.message_service.entities.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message")
class MessageController(
    @Autowired
    private val messageService: MessageService
) {

    @GetMapping("/history")
    fun getHistory(@RequestParam senderId: String, @RequestParam receiverId: String): List<Message>{
        return messageService.getHistory(senderId, receiverId)
    }
}