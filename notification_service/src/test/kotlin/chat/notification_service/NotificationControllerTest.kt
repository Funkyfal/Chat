package chat.notification_service
import chat.notification_service.controllers.NotificationController
import chat.notification_service.dto.NotificationRequest
import chat.notification_service.entities.Notification
import chat.notification_service.exceptions.UnauthorizedException
import chat.notification_service.services.NotificationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class NotificationControllerTest {

    private lateinit var notificationService: NotificationService
    private lateinit var notificationController: NotificationController

    @BeforeEach
    fun setUp() {
        notificationService = mock(NotificationService::class.java)
        notificationController = NotificationController(notificationService)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `markAsRead - success, if user authorized with receiverId`() {
        val currentUser = "receiver"
        val auth = UsernamePasswordAuthenticationToken(currentUser, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val response: ResponseEntity<Void> = notificationController.markAsRead("sender", "receiver")

        verify(notificationService).markNotificationAsRead(NotificationRequest("sender", "receiver"))
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `markAsRead - thrown UnauthorizedException, if user doesn't match with receiverId`() {
        val auth = UsernamePasswordAuthenticationToken("otherUser", null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val exception = assertThrows(UnauthorizedException::class.java) {
            notificationController.markAsRead("sender", "receiver")
        }
        assertEquals("Вы не можете пометить сообщения receiver прочитанными, так как вы авторизованы как otherUser", exception.message)
    }

    @Test
    fun `getAllNotifications - success, if user authorized with receiverId`() {
        val currentUser = "receiver"
        val auth = UsernamePasswordAuthenticationToken(currentUser, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val notifications = listOf(
            Notification(
                id = "1", senderId = "sender", receiverId = "receiver",
                messagePreview = "Привет", timestamp = System.currentTimeMillis(), read = false
            )
        )
        `when`(notificationService.findAllNotifications("receiver")).thenReturn(notifications)

        val response: ResponseEntity<List<Notification>> = notificationController.getAllNotifications("receiver")

        verify(notificationService).findAllNotifications("receiver")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notifications, response.body)
    }

    @Test
    fun `getAllNotifications - thrown UnauthorizedException, if user doesn't match with receiverId`() {
        val auth = UsernamePasswordAuthenticationToken("otherUser", null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val exception = assertThrows(UnauthorizedException::class.java) {
            notificationController.getAllNotifications("receiver")
        }
        assertEquals("Вы не можете просмотреть историю уведомлений receiver, так как вы авторизованы как otherUser", exception.message)
    }
}