package com.example.SecurityPractice.Controller;

import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.Model.User;
import com.example.SecurityPractice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("")
    public ResponseEntity<?> insertUser(
            @Valid @RequestBody UserDTO dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.saveOrUpdateUser(dto.toEntity());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
