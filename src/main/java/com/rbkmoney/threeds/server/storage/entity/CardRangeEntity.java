package com.rbkmoney.threeds.server.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_range")
public class CardRangeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "range_start")
    private long rangeStart;

    @Column(name = "range_end")
    private long rangeEnd;
}
