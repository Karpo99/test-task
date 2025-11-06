package com.userservice.config;

import com.userservice.utils.KeyConverter;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TokenConfig {


    @Value("${auth.access-token-expire-minutes:30}")
    private int accessTokenExpireMinutes;

    @Value("${auth.refresh-token-expire-days:1}")
    private int refreshTokenExpireDays;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public TokenConfig(@Value("${auth.public-key}") String rawPublicKey, @Value("${auth.private-key}") String rawPrivateKey) {
        this.publicKey = KeyConverter
                .convertPublicKey(rawPublicKey);

        this.privateKey = KeyConverter
                .convertPrivateKey(rawPrivateKey);
    }
}
