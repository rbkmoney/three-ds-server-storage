package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.RReqTransactionInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RReqTransactionInfoRepository extends JpaRepository<RReqTransactionInfoEntity, String> {

}
