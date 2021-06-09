package com.brewery.beerorderservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderDto extends BaseEntity{

    @Builder
    public BeerOrderDto(UUID uuid, Long version, OffsetDateTime createdDate, OffsetDateTime updatedDate, UUID customerId, String customerRef, List<BeerOrderLineDto> beerOrderLines, OrderStatusEnum orderStatus, String orderStatusCallbackUrl) {
        super(uuid, version, createdDate, updatedDate);
        this.customerRef = customerRef;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;
    private List<BeerOrderLineDto> beerOrderLines;
    private OrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;


}
