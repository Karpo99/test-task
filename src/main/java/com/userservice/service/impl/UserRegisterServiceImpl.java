package com.userservice.service.impl;

import com.userservice.exception.UserAlreadyExistException;
import com.userservice.model.dto.request.UserRegisterRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.enums.UserType;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {
    private static final String EXCEPTION_MESSAGE = "The email are already in use!";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void registerUser(UserRegisterRequest request) {
        log.info("User registration attempt for email: {}", request.getEmail());

        checkEmailAvailability(request.getEmail());

        UserType userType = isAdminEmail(request.getEmail());
        log.info("User type determined as: {}", userType);

        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(userType)
                .build();

        userRepository.save(userEntity);
        log.info("User successfully registered: {}", request.getEmail());

    }

    private void checkEmailAvailability(final String email) {
        log.debug("Checking if email is already taken: {}", email);

        if (userRepository.existsUserEntityByEmail(email)) {
            log.warn("Registration failed: Email {} is already in use.", email);
            throw new UserAlreadyExistException(EXCEPTION_MESSAGE);
        }
    }

    private UserType isAdminEmail(final String email) {
        log.debug("Checking if email {} belongs to an admin", email);

        if (email.equals(adminEmail)) {
            log.info("Admin email detected: {}", email);
            return UserType.ADMIN;
        }
        return UserType.USER;
    }
}
