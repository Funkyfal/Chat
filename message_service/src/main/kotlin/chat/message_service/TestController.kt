package chat.message_service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(@Autowired private val messageRepository: MessageRepository) {

    @GetMapping("/getAllMessages")
    fun getAll(): MutableList<Message> {
        return messageRepository.findAll()
    }

    @PostMapping("newMessage")
    fun newMessage(@RequestBody message: Message): Message {
        return messageRepository.save(message)
    }
}