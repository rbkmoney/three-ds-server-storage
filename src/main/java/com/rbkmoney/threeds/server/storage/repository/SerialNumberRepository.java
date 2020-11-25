package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, String> {

    Optional<SerialNumberEntity> findByProviderId(String providerId);

}
