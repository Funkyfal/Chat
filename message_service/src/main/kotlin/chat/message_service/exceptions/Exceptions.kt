package chat.message_service.exceptions

class UnauthorizedException(message: String?): RuntimeException(message)

class MessageProcessingException(message: String?): RuntimeException(message)