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
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "last_updated")
public class LastUpdatedEntity implements Serializable {

    @Id
    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

}
