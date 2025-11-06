package com.userservice.controller;

import com.userservice.model.Token;
import com.userservice.model.dto.request.TokenInvalidateRequest;
import com.userservice.model.dto.request.TokenRefreshRequest;
import com.userservice.model.dto.request.UserLoginRequest;
import com.userservice.model.dto.request.UserRegisterRequest;
import com.userservice.service.RefreshTokenService;
import com.userservice.service.TokenService;
import com.userservice.service.UserLoginService;
import com.userservice.service.UserLogoutService;
import com.userservice.service.UserRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Management",
        description = "Endpoints for user registration, login, authentication, and token management")
public class UserController {
    private final UserRegisterService userRegisterService;
    private final UserLoginService userLoginService;
    private final RefreshTokenService refreshTokenService;
    private final UserLogoutService userLogoutService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user in the system.")
    public ResponseEntity<Void> registerUser(@RequestBody @Validated final UserRegisterRequest request) {
        log.info("Received user registration request for email: {}", request.getEmail());

        userRegisterService.registerUser(request);

        log.info("User successfully registered: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT amd refresh tokens.")
    public ResponseEntity<Token> login(@RequestBody @Valid final UserLoginRequest request) {
        log.info("User attempting login: {}", request.email());

        Token token = userLoginService.login(request);

        log.info("Login successful for user: {}", request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes the JWT token using a refresh token.")
    public ResponseEntity<Token> refresh(@RequestBody @Valid final TokenRefreshRequest request) {
        log.info("Token refresh request received");

        Token newToken = refreshTokenService.refreshToken(request);

        log.info("Token successfully refreshed");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newToken);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the user's token to log out.")
    public ResponseEntity<Void> logout(@RequestBody @Valid TokenInvalidateRequest request) {
        log.info("Logout request received for token: {}", request.accessToken());

        userLogoutService.logout(request);

        log.info("User successfully logged out.");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping API", description = "Returns 'pong' to check API status.")
    public ResponseEntity<String> pong() {
        log.debug("Ping endpoint hit.");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("pong!!!!");
    }

    @GetMapping("/authenticate")
    @Operation(summary = "Receive authenticate user", description = "Returns authentication details from a JWT token.")
    public ResponseEntity<UsernamePasswordAuthenticationToken> getAuthentication(@RequestParam String token) {
        log.info("Authentication request received for token.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokenService.getAuthentication(token));
    }
}
