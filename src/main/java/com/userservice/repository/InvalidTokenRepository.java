package com.userservice.repository;

import com.userservice.model.entity.InvalidTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {
    Optional<InvalidTokenEntity> findInvalidTokenEntityById(final String tokenId);
}
