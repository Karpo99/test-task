package com.userservice.service.impl;

import com.userservice.config.TokenConfig;
import com.userservice.model.Token;
import com.userservice.model.enums.TokenClaims;
import com.userservice.model.enums.TokenType;
import com.userservice.model.enums.UserType;
import com.userservice.service.InvalidTokenService;
import com.userservice.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenConfig tokenConfig;
    private final InvalidTokenService invalidTokenService;

    @Override
    public Token generateToken(Map<String, Object> claims) {
        final long currentTimeMills = System.currentTimeMillis();

        final Date tokenIssuedAt = new Date(currentTimeMills);

        final Date accessTokenExpiredAt = DateUtils
                .addMinutes(new Date(currentTimeMills),
                        tokenConfig
                                .getAccessTokenExpireMinute());

        final String accessToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(tokenIssuedAt)
                .expiration(accessTokenExpiredAt)
                .signWith(tokenConfig.getPrivateKey())
                .claims(claims)
                .compact();

        final Date refreshTokenExpiresAt = DateUtils
                .addDays(new Date(currentTimeMills),
                        tokenConfig
                                .getRefreshTokenExpireDay());

        final String refreshToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(tokenIssuedAt)
                .expiration(refreshTokenExpiresAt)
                .signWith(tokenConfig.getPrivateKey())
                .claim(TokenClaims.USER_ID.getValue(), claims.get(TokenClaims.USER_ID.getValue()))
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiredAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public Token generateToken(Map<String, Object> claims, String refreshToken) {
        final long currentTimeMills = System.currentTimeMillis();

        final String refreshTokenId = this.getId(refreshToken);

        invalidTokenService.checkForInvalidityOfToken(refreshTokenId);

        final Date accessTokenIssuedAt = new Date(currentTimeMills);

        final Date accessTokenExpiredAt = DateUtils
                .addMinutes(new Date(currentTimeMills),
                        tokenConfig
                                .getAccessTokenExpireMinute());

        final String accessToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(accessTokenIssuedAt)
                .expiration(accessTokenExpiredAt)
                .signWith(tokenConfig.getPrivateKey())
                .claims(claims)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiredAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        final Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(tokenConfig.getPublicKey())
                .build()
                .parseSignedClaims(token);

        final Claims payload = claimsJws.getPayload();
        final JwsHeader jwsHeader = claimsJws.getHeader();

        final Jwt jwt = new Jwt(
                token,
                payload.getIssuedAt().toInstant(),
                payload.getExpiration().toInstant(),
                Map.of(
                        TokenClaims.TYP.getValue(), jwsHeader.getType(),
                        TokenClaims.ALGORITHM.getValue(), jwsHeader.getAlgorithm()
                ),
                payload
        );

        final UserType userType = UserType
                .valueOf(payload.get(TokenClaims.USER_TYPE.getValue()).toString());

        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(userType.name()));

        log.info("Returning authentication for token {}", jwt);
        return UsernamePasswordAuthenticationToken.authenticated(jwt, null, authorities);
    }

    @Override
    public void verifyAndValidate(String jwt) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(tokenConfig.getPublicKey())
                    .build()
                    .parseSignedClaims(jwt);

            Claims claims = claimsJws.getPayload();

            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Token has expired");
            }
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired", e);
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating token", e);
        }
    }

    @Override
    public void verifyAndValidate(Set<String> jwts) {
        jwts.forEach(this::verifyAndValidate);
    }

    @Override
    public Jws<Claims> getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfig.getPublicKey())
                .build()
                .parseSignedClaims(jwt);
    }

    @Override
    public Claims getPayload(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfig.getPublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    @Override
    public String getId(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfig.getPublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getId();
    }
}
