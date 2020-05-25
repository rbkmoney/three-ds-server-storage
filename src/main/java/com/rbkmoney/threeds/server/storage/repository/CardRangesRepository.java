package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRangesRepository extends JpaRepository<CardRangeEntity, Long> {

    List<CardRangeEntity> findByProviderId(String providerId);
}
