package com.userservice.service;

import com.userservice.model.dto.request.UserRegisterRequest;

public interface UserRegisterService {
    void registerUser(final UserRegisterRequest request);
}
