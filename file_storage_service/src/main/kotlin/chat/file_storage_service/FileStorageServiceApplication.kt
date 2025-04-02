package chat.file_storage_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FileStorageServiceApplication

fun main(args: Array<String>) {
	runApplication<FileStorageServiceApplication>(*args)
}
