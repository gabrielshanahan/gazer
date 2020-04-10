package io.github.gabrielshanahan.gazer.app.controller

import io.github.gabrielshanahan.gazer.data.model.User
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping(path = ["/user"])
class UserController(private val userRepository: UserRepository) {

    @get:ResponseBody
    @get:GetMapping(path = ["/all"])
    val allUsers: Iterable<User?>
        get() = userRepository.findAll()
}
