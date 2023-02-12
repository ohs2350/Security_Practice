package com.example.SecurityPractice.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @PostMapping("/userLogin")
    public String login() {

        return "index";
    }
}
