package com.brewery.beerorderservice.web.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto extends BaseEntity{

    public CustomerDto(UUID uuid, Long version, OffsetDateTime createdDate, OffsetDateTime updatedDate, String name) {
        super(uuid, version, createdDate, updatedDate);
        this.name = name;
    }

    private String name;
}
