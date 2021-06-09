package com.brewery.beerorderservice.web.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusUpdate extends BaseEntity {

    private UUID orderId;
    private String customerRef;
    private String orderStatus;
}
