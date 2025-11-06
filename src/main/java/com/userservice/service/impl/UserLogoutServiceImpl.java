package com.userservice.service.impl;

import com.userservice.model.dto.request.TokenInvalidateRequest;
import com.userservice.service.InvalidTokenService;
import com.userservice.service.TokenService;
import com.userservice.service.UserLogoutService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogoutServiceImpl implements UserLogoutService {
    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

    @Override
    public void logout(TokenInvalidateRequest request) {
        log.info("Logout request received for tokens.");

        tokenService.verifyAndValidate(Set.of(request.accessToken(), request.refreshToken()));
        log.info("Tokens successfully verified.");

        final String accessTokenId = tokenService.getPayload(request.accessToken()).getId();
        log.info("Extracted access token ID: {}", accessTokenId);

        invalidTokenService.checkForInvalidityOfToken(accessTokenId);
        log.debug("Checked invalidity status of access token: {}", accessTokenId);

        final String refreshTokenId = tokenService.getPayload(request.refreshToken()).getId();
        log.info("Extracted refresh token ID: {}", refreshTokenId);

        invalidTokenService.checkForInvalidityOfToken(refreshTokenId);
        log.debug("Checked invalidity status of refresh token: {}", refreshTokenId);

        invalidTokenService.invalidateTokens(Set.of(accessTokenId, refreshTokenId));
        log.info("Tokens invalidated successfully: {}, {}", accessTokenId, refreshTokenId);
    }
}
