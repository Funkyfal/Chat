plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Chat"

include("auth_service")
include("chat_service")
include("message_service")
include("notification_service")
include("file_storage_service")
