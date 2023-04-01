package com.web.paygate237.controllers;

import com.web.paygate237.models.User;
import com.web.paygate237.requests.UserRequest;
import com.web.paygate237.services.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public User signup(@RequestBody UserRequest userRequest) throws UnsupportedEncodingException, MessagingException {
        return userService.signupUser(userRequest);
    }

}
