package com.userservice.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@Builder
public class Token {
    private static final String TOKEN_PREFIX = "Bearer ";
    private String accessToken;
    private Long accessTokenExpiresAt;
    private String refreshToken;

    public static boolean isBearerToken(final String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(TOKEN_PREFIX);
    }

    public static String getJwtFromHeader(final String authorizationHeader) {
        return authorizationHeader.replace(TOKEN_PREFIX, "");
    }
}
