package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LastUpdatedRepository extends JpaRepository<LastUpdatedEntity, String> {

    Optional<LastUpdatedEntity> findByProviderId(String providerId);

}
