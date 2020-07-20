package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerialNumRepository extends JpaRepository<SerialNumEntity, String> {

    Optional<SerialNumEntity> findByProviderId(String providerId);

}
