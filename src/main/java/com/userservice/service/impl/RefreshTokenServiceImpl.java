package com.userservice.service.impl;

import com.userservice.exception.UserNotFoundException;
import com.userservice.exception.UserStatusNotValidException;
import com.userservice.model.Token;
import com.userservice.model.dto.request.TokenRefreshRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.enums.TokenClaims;
import com.userservice.model.enums.UserStatus;
import com.userservice.repository.UserRepository;
import com.userservice.service.RefreshTokenService;
import com.userservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public Token refreshToken(TokenRefreshRequest request) {
        log.info("Token refresh request received for refreshToken: {}", request.refreshToken());

        tokenService.verifyAndValidate(request.refreshToken());
        log.info("Refresh token successfully verified.");

        final String userId = tokenService
                .getPayload(request.refreshToken())
                .get(TokenClaims.USER_ID.getValue())
                .toString();
        log.info("Extracted user ID from refresh token: {}", userId);

        final UserEntity userEntityFromDb = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for ID: {}", userId);
                    return new UserNotFoundException();
                });

        validateUserStatus(userEntityFromDb);

        log.info("Generating new token for user ID: {}", userId);
        return tokenService.generateToken(userEntityFromDb.getUserClaims(), request.refreshToken());
    }

    private void validateUserStatus(UserEntity userEntityFromDb) {
        log.debug("Validating user status for user ID: {}", userEntityFromDb.getId());

        if (!(UserStatus.ACTIVE.equals(userEntityFromDb.getUserStatus()))) {
            log.warn("User status not valid for user ID: {}, status: {}",
                    userEntityFromDb.getId(), userEntityFromDb.getUserStatus());

            throw new UserStatusNotValidException("User status: " + userEntityFromDb.getUserStatus());
        }
    }
}
