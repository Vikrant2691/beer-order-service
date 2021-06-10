package com.brewery.beerorderservice.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BeerOrderLine extends BaseEntity {

    @Builder
    public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp updatedDate, BeerOrder beerOrder, Integer orderQuantity, Integer quantityAllocated) {
        super(id, version, createdDate, updatedDate);
        this.beerOrder = beerOrder;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private BeerOrder beerOrder;
    private String upc;
    private String beerName;
    private Integer orderQuantity;
    private Integer quantityAllocated;

}
