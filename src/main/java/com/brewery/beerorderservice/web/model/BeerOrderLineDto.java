package com.brewery.beerorderservice.web.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderLineDto extends BaseEntity {

    @Builder
    public BeerOrderLineDto(UUID uuid, Long version, OffsetDateTime createdDate, OffsetDateTime updatedDate, String upc, String beerName, Integer orderQuantity, Integer quantityAllocated) {
        super(uuid, version, createdDate, updatedDate);
        this.upc = upc;
        this.beerName = beerName;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    private String upc;
    private String beerName;
    private Integer orderQuantity;
    private Integer quantityAllocated;
}
