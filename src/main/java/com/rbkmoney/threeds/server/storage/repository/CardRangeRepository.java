package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface CardRangeRepository extends JpaRepository<CardRangeEntity, CardRangePk> {

    @Transactional
    void deleteAllByPkProviderId(String providerId);

    List<CardRangeEntity> findByPkProviderId(String providerId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "30000")})
    @Query("SELECT cr FROM CardRangeEntity cr WHERE cr.pk.providerId=?1 AND cr.pk IN ?2")
    List<CardRangeEntity> findByIdsWithPessimisticLocking(String providerId, Set<CardRangePk> cardRangePks);

    boolean existsCardRangeEntitiesByPkProviderIdIs(String providerId);

    @Query("select " +
            "case when count(cr)> 0 then false else true end " +
            "from CardRangeEntity cr " +
            "where cr.pk.providerId = ?1 " +
            "and ((cr.pk.rangeStart > ?2 and cr.pk.rangeStart <= ?3) " +
            "or (cr.pk.rangeEnd >= ?2 and cr.pk.rangeEnd < ?3) " +
            "or (cr.pk.rangeEnd < ?3 and cr.pk.rangeStart > ?2) " +
            "or (cr.pk.rangeEnd > ?3 and cr.pk.rangeStart < ?2)" +
            "or (cr.pk.rangeEnd = ?2 and cr.pk.rangeEnd = ?3) " +
            "or (cr.pk.rangeStart = ?2 and cr.pk.rangeStart = ?3))"
    )
    boolean existsFreeSpaceForNewCardRange(String providerId, long startRange, long endRange);

    boolean existsCardRangeEntityByPkEquals(CardRangePk cardRangePk);

    @Query("select " +
            "cr.pk.providerId " +
            "from CardRangeEntity cr " +
            "where cr.pk.rangeEnd >= ?1 and cr.pk.rangeStart <= ?1")
    Page<String> getProviderIds(long accountNumber, Pageable pageable);

    @Query("select " +
            "cr " +
            "from CardRangeEntity cr " +
            "where cr.pk.rangeEnd >= ?1 and cr.pk.rangeStart <= ?1")
    Page<CardRangeEntity> getCardRangeEntities(long accountNumber, Pageable pageable);

    @Transactional
    default void saveOrUpdateWithPessimisticLocking(String providerId, List<CardRangeEntity> cardRangeEntities) {
        if (existsCardRangeEntitiesByPkProviderIdIs(providerId)) {
            Set<CardRangePk> cardRangePks = cardRangeEntities.stream()
                    .map(CardRangeEntity::getPk)
                    .collect(Collectors.toSet());

            findByIdsWithPessimisticLocking(providerId, cardRangePks);
        }

        saveAll(cardRangeEntities);
    }
}
