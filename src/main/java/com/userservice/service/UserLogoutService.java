package com.userservice.service;

import com.userservice.model.dto.request.TokenInvalidateRequest;

public interface UserLogoutService {
    void logout(final TokenInvalidateRequest request);
}
