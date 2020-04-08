package io.github.gabrielshanahan.gazer.controller

import io.github.gabrielshanahan.gazer.model.User
import io.github.gabrielshanahan.gazer.repository.UserRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@RequestMapping(path = ["/user"])
class MainController(private val userRepository: UserRepository) {

    @get:ResponseBody
    @get:GetMapping(path = ["/all"])
    val allUsers: Iterable<User?>
        get() = userRepository.findAll()
}
