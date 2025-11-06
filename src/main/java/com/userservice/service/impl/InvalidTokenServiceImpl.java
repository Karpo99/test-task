package com.userservice.service.impl;

import com.userservice.exception.TokenAlreadyInvalidatedException;
import com.userservice.model.entity.InvalidTokenEntity;
import com.userservice.repository.InvalidTokenRepository;
import com.userservice.service.InvalidTokenService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvalidTokenServiceImpl implements InvalidTokenService {

    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    public void invalidateTokens(Set<String> tokenIds) {
        final Set<InvalidTokenEntity> invalidTokenEntities = tokenIds.stream()
                .map(tokenId -> InvalidTokenEntity.builder()
                        .tokenId(tokenId)
                        .build())
                .collect(Collectors.toSet());

        invalidTokenRepository.saveAll(invalidTokenEntities);
    }

    @Override
    public void checkForInvalidityOfToken(String tokenId) {
        final boolean isTokenInvalid = invalidTokenRepository
                .findInvalidTokenEntityById(tokenId).isPresent();

        if (isTokenInvalid) {
            throw new TokenAlreadyInvalidatedException(tokenId);
        }
    }
}
