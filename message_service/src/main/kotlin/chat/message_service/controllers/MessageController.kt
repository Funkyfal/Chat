package chat.message_service.controllers

import chat.message_service.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message")
class MessageController(
    @Autowired
    private val messageService: MessageService
) {

    @GetMapping("/history")
    fun getHistory(@RequestParam receiverId: String): ResponseEntity<Any>{
        val currentUser = SecurityContextHolder.getContext().authentication?.name
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok(messageService.getHistory(currentUser, receiverId))
    }
}