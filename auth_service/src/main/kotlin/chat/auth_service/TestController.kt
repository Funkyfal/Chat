package chat.auth_service

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController (private val testRepository: testRepository) {

    @GetMapping("/getAll")
    fun getAll(): List<Test>{
        return testRepository.findAll()
    }
}