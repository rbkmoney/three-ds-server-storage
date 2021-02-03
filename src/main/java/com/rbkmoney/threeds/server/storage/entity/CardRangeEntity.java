package com.rbkmoney.threeds.server.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_range")
public class CardRangeEntity implements Serializable {

    @EmbeddedId
    private CardRangePk pk;

    @Column(name = "acs_start_protocol_version")
    private String acsStartProtocolVersion;

    @Column(name = "acs_end_protocol_version")
    private String acsEndProtocolVersion;

    @Column(name = "ds_start_protocol_version")
    private String dsStartProtocolVersion;

    @Column(name = "ds_end_protocol_version")
    private String dsEndProtocolVersion;

    @Column(name = "acs_information_indicator")
    private String acsInformationIndicator;

    @Column(name = "three_ds_method_url")
    private String threeDsMethodUrl;

}
