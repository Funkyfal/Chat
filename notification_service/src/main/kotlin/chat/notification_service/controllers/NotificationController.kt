package chat.notification_service.controllers

import chat.notification_service.dto.NotificationRequest
import chat.notification_service.entities.Notification
import chat.notification_service.services.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController (
    private val notificationService: NotificationService
){
    //возможно будут прочитаны не все уведомления, надо будет проверить
    @PutMapping("/markAsRead")
    fun markAsRead(@RequestParam senderId: String, @RequestParam receiverId: String): ResponseEntity<Void>{
        val currentUser = SecurityContextHolder.getContext().authentication?.name
        if(currentUser != receiverId) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        notificationService.markNotificationAsRead(NotificationRequest(senderId, receiverId))
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/getNotifications")
    fun getAllNotifications(@RequestParam receiverId: String): ResponseEntity<List<Notification>>{
        val currentUser = SecurityContextHolder.getContext().authentication?.name
        if(currentUser != receiverId) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok(notificationService.findAllNotifications(receiverId))
    }
}