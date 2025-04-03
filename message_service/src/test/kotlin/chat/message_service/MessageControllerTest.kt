package chat.message_service

import chat.message_service.controllers.MessageController
import chat.message_service.exceptions.UnauthorizedException
import chat.message_service.services.MessageService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class MessageControllerTest {

    private val messageService: MessageService = mockk()
    private lateinit var messageController: MessageController

    @BeforeEach
    fun setup() {
        messageController = MessageController(messageService)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `getHistory should throw UnauthorizedException when authentication is missing`() {
        SecurityContextHolder.clearContext()

        val receiverId = "userB"
        val exception = assertThrows(UnauthorizedException::class.java) {
            messageController.getHistory(receiverId)
        }
        assertTrue(exception.message!!.contains(receiverId))
    }

    @Test
    fun `getHistory should return history when user is authenticated`() {
        val currentUser = "userA"
        SecurityContextHolder.getContext().authentication =
            TestingAuthenticationToken(currentUser, null)

        val receiverId = "userB"
        val expectedHistory = listOf(
            chat.message_service.entities.Message(
                id = "1", text = "Hello", senderId = currentUser, receiverId = receiverId, timestamp = 123456789L
            )
        )
        every { messageService.getHistory(currentUser, receiverId) } returns expectedHistory

        val response: ResponseEntity<Any> = messageController.getHistory(receiverId)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedHistory, response.body)

        verify(exactly = 1) { messageService.getHistory(currentUser, receiverId) }
    }
}