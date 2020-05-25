package com.rbkmoney.threeds.server.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rreq_transaction_info")
public class RReqTransactionInfoEntity implements Serializable {

    @Id
    private String id;

    @Column(name = "device_channel")
    private String deviceChannel;

    @Column(name = "decoupled_auth_max_time")
    private Date decoupledAuthMaxTime;

    @Column(name = "acs_dec_con_ind")
    private String acsDecConInd;
}
