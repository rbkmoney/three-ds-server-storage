package com.rbkmoney.threeds.server.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "serial_number")
public class SerialNumberEntity implements Serializable {

    @Id
    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "serial_number")
    private String serialNumber;

}
