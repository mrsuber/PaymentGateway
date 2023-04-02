package com.web.paygate237.controllers;

import com.web.paygate237.models.User;
import com.web.paygate237.models.VerifyUser;
import com.web.paygate237.requests.UserRequest;
import com.web.paygate237.requests.VerifyRequest;
import com.web.paygate237.services.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserRequest userRequest) throws UnsupportedEncodingException, MessagingException {
        return ResponseEntity.ok(userService.signupUser(userRequest));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@RequestBody VerifyRequest verifyRequest) {
        HttpStatus verifiedStatus = userService.verifyUser(verifyRequest);

        return switch (verifiedStatus) {
            case NOT_FOUND -> ResponseEntity.status(verifiedStatus).body(Map.of(
                    "message", "Sorry, no such user"
            ));
            case UNAUTHORIZED -> ResponseEntity.status(verifiedStatus).body(Map.of(
                    "message", "Sorry, Verification Code has expired!"
            ));
            case OK -> ResponseEntity.status(verifiedStatus).body(Map.of("message", "Successful Verification!"));
            case CONFLICT -> ResponseEntity.status(verifiedStatus).body(Map.of("message", "User already activated"));
            default -> null;
        };
    }

//    @GetMapping("/generate-new-code")

}
