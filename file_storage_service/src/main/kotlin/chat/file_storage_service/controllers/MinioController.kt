package chat.file_storage_service.controllers

import chat.file_storage_service.services.MinioService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class MinioController(
    private val minioService: MinioService
) {
    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val fileUrl = minioService.uploadFile(file)
        return ResponseEntity.ok(fileUrl)
    }

    @GetMapping("/{fileName}")
    fun downloadFile(@PathVariable fileName: String): ResponseEntity<ByteArray> {
        val (fileBytes, contentType) = minioService.downloadFile(fileName)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(fileBytes)
    }
}