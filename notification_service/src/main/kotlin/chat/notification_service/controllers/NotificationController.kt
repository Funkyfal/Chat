package chat.notification_service.controllers

import chat.notification_service.dto.NotificationRequest
import chat.notification_service.entities.Notification
import chat.notification_service.services.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController (
    private val notificationService: NotificationService
){
    //возможно будут прочитаны не все уведомления, надо будет проверить
    @PutMapping("/markAsRead")
    fun markAsRead(@RequestBody request: NotificationRequest): ResponseEntity<Void>{
        notificationService.markNotificationAsRead(request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/getNotifications")
    fun getAllNotifications(request: NotificationRequest): List<Notification>{
        return notificationService.findAllNotifications(request.senderId)
    }
}