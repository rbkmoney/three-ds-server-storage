package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRangeRepository extends JpaRepository<CardRangeEntity, CardRangePk> {

    List<CardRangeEntity> findByPkProviderId(String providerId);
}
