package com.userservice.service.impl;

import com.userservice.exception.PasswordNotValidException;
import com.userservice.model.Token;
import com.userservice.model.dto.request.UserLoginRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.repository.UserRepository;
import com.userservice.service.TokenService;
import com.userservice.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
    private static final String EXCEPTION_MESSAGE = "Can't find user with given email: ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public Token login(UserLoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        UserEntity userEntityFromDb = userRepository
                .findUserEntityByEmail(request.email())
                .orElseThrow(
                        () -> new UsernameNotFoundException(EXCEPTION_MESSAGE + request.email()));

        if (Boolean.FALSE.equals(passwordEncoder.matches(request.password(),
                userEntityFromDb.getPassword()))) {
            log.warn("Login failed: Incorrect password for email: {}", request.email());

            throw new PasswordNotValidException();
        }
        log.info("Login successful for email: {}", request.email());
        return tokenService.generateToken(userEntityFromDb.getUserClaims());
    }
}
