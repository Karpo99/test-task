package com.userservice.service;

import com.userservice.model.Token;
import com.userservice.model.dto.request.UserLoginRequest;

public interface UserLoginService {
    Token login(final UserLoginRequest request);
}
