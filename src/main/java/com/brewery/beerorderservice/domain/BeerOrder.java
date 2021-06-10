package com.brewery.beerorderservice.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class BeerOrder extends BaseEntity {

    @Builder
    public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp updatedDate, String customerRef, Set<BeerOrderLine> beerOrders, OrderStatusEnum orderStatus, String orderStatusCallbackUrl, Customer customer) {
        super(id, version, createdDate, updatedDate);
        this.customerRef = customerRef;
        this.beerOrders = beerOrders;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.customer = customer;
    }

    private String customerRef;
    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    Set<BeerOrderLine> beerOrders = new HashSet<>();
    private OrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;

    @ManyToOne
    Customer customer;

}
