package beer.order.service.domain;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class BeerOrder {

    @Builder
    public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp updatedDate, String customerRef, Customer customer, Set<BeerOrderLine> beerOrderLines, BeerOrderStatusEnum orderStatus, String orderStatusCallbackUrl) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.customerRef = customerRef;
        this.customer = customer;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp updatedDate;

    public boolean isNew() {
        return this.id == null;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderLine> beerOrderLines;

    private BeerOrderStatusEnum orderStatus = BeerOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;

    @Override
    public String toString() {
        return "BeerOrder{" +
                "id=" + id +
                ", version=" + version +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", customerRef='" + customerRef + '\'' +
//                ", customer=" + customer +
                ", beerOrderLines=" + beerOrderLines +
                ", orderStatus=" + orderStatus +
                ", orderStatusCallbackUrl='" + orderStatusCallbackUrl + '\'' +
                '}';
    }
}
