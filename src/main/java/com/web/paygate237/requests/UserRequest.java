package com.web.paygate237.requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private int phoneNumber;
}
