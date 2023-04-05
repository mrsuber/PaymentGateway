package com.web.paygate237.controllers;

import com.web.paygate237.models.User;
import com.web.paygate237.requests.NewCodeRequest;
import com.web.paygate237.requests.UserRequest;
import com.web.paygate237.requests.VerifyRequest;
import com.web.paygate237.services.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody UserRequest userRequest) throws UnsupportedEncodingException, MessagingException {
        Map<String, ?> signupResponse = userService.signupUser(userRequest);
        return switch ((HttpStatus) signupResponse.get("status")) {
            case OK -> ResponseEntity.status((HttpStatusCode) signupResponse.get("status")).body(Map.of("user", signupResponse.get("user")));
            case BAD_REQUEST -> ResponseEntity.status((HttpStatusCode) signupResponse.get("status")).body(Map.of("message", signupResponse.get("message")));
            case CONFLICT -> ResponseEntity.status((HttpStatusCode) signupResponse.get("status")).body(Map.of("message", signupResponse.get("message")));
            default -> null;
        };
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

    @PostMapping("/create-new-code")
    public ResponseEntity<Object> createNewCode(@RequestBody NewCodeRequest newCodeRequest) {
        HttpStatus newCodeStatus = userService.generateNewCode(newCodeRequest);

        return switch (newCodeStatus) {
            case NOT_FOUND -> ResponseEntity.status(newCodeStatus).body(Map.of("message", "User not found!"));
            case OK -> ResponseEntity.status(newCodeStatus).body(Map.of("message", "Email Sent Successfully"));
            case SERVICE_UNAVAILABLE -> ResponseEntity.status(newCodeStatus).body(Map.of("message", "A Server Error Occured!"));
            case CONFLICT -> ResponseEntity.status(newCodeStatus).body(Map.of("message", "User is already activated!"));
            default -> null;
        };
    }

}
