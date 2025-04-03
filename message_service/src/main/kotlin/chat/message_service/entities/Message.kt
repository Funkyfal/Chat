package chat.message_service.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "messages")
data class Message @JsonCreator constructor(
    @JsonProperty("id") @Id val id: String? = null,
    @JsonProperty("text") val text: String?,
    @JsonProperty("fileUrl") val fileUrl: String? = null,
    @JsonProperty("senderId") val senderId: String,
    @JsonProperty("receiverId") val receiverId: String,
    @JsonProperty("timestamp") val timestamp: Long
)