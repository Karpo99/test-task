package com.userservice.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.config.TokenConfigurationParameter;
import com.userservice.model.Token;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.enums.TokenClaims;
import com.userservice.model.enums.UserStatus;
import com.userservice.model.enums.UserType;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.lang3.time.DateUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractRestControllerTest extends AbstractTestContainerConfiguration {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Token mockUserToken;

    @Mock
    private TokenConfigurationParameter configurationParameter;

    @BeforeEach
    public void initializeAuth() {
        UserEntity mockUser = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .email("karpo99old@gmail.com")
                .password("password")
                .firstName("Bob")
                .lastName("Boson")
                .userType(UserType.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        this.configurationParameter = new TokenConfigurationParameter();
        this.mockUserToken = this.generate(mockUser.getUserClaims());
    }

    private Token generate(Map<String, Object> claims) {
        final long currentTimeMills = System.currentTimeMillis();

        final Date tokenIssuedAt = new Date(currentTimeMills);

        final Date accessTokenExpiresAt = DateUtils
                .addMinutes(new Date(currentTimeMills),
                        configurationParameter.getAccessTokenExpireMinute());

        final String accessToken = Jwts.builder()
                .header()
                .add(TokenClaims.TYP.getValue(), OAuth2AccessToken.TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(tokenIssuedAt)
                .expiration(accessTokenExpiresAt)
                .signWith(configurationParameter.getPrivateKey())
                .claims(claims)
                .compact();

        final Date refreshTokenExpiresAt = DateUtils
                .addDays(new Date(currentTimeMills),
                        configurationParameter.getRefreshTokenExpireDay());

        final String refreshToken = Jwts.builder()
                .header()
                .add(TokenClaims.TYP.getValue(), OAuth2AccessToken.TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(tokenIssuedAt)
                .expiration(refreshTokenExpiresAt)
                .signWith(configurationParameter.getPrivateKey())
                .claim(TokenClaims.USER_ID.getValue(), claims.get(TokenClaims.USER_ID.getValue()))
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();

    }
}
