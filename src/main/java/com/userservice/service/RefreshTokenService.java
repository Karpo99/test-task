package com.userservice.service;

import com.userservice.model.Token;
import com.userservice.model.dto.request.TokenRefreshRequest;

public interface RefreshTokenService {
    Token refreshToken(final TokenRefreshRequest request);
}
