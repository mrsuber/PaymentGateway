package com.web.paygate237.services;

import com.web.paygate237.models.User;
import com.web.paygate237.requests.UserRequest;

import java.util.Optional;

public interface AuthService {

    Optional<User> AddUser(UserRequest userRequest);

}
