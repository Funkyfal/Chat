package chat.auth_service

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/user/hello")
    fun greetingUser(): String{
        return "hello my dear user"
    }

    @GetMapping("/admin/hello")
    fun greetingAdmin(): String{
        return "hello my dear admin!"
    }
}