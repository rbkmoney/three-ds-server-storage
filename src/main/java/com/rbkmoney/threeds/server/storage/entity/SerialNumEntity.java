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
@Table(name = "serial_num")
public class SerialNumEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "serial_num")
    private String serialNum;
}
