package com.rbkmoney.threeds.server.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRangePk implements Serializable {

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "range_start")
    private long rangeStart;

    @Column(name = "range_end")
    private long rangeEnd;
}
