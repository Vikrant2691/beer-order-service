package beer.order.service.web.mappers;

import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderLine;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.domain.Customer;
import beer.order.service.repository.CustomerRepository;
import beer.order.service.web.model.BeerOrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerOrderModelMapperTest {

    @Autowired
    BeerOrderModelMapper beerOrderModelMapper;

    @Autowired
    CustomerRepository customerRepository;

    Customer testCustomer;

    UUID beerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {

    }

    @Test
    void convertToDTO() {


        BeerOrderDto beerOrderDto = beerOrderModelMapper.convertToDTO(createBeerOrder());
        System.out.println(beerOrderDto.toString());


    }

    @Test
    void convertToEntity() {
    }

    public BeerOrder createBeerOrder() {

        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());

        BeerOrder beerOrder = new BeerOrder();


        beerOrder.setId(beerId);
        beerOrder.setCustomer(testCustomer);
        beerOrder.setVersion(1L);
        beerOrder.setCustomerRef("Customer Ref");
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        beerOrder.setOrderStatusCallbackUrl("orderStatusCallbackUrl");


        BeerOrderLine beerOrderLine = new BeerOrderLine();

        beerOrderLine.setBeerId(beerId);


        beerOrderLine.setBeerOrder(beerOrder);
        beerOrderLine.setOrderQuantity(1);

        System.out.println(beerOrder.toString());
        beerOrder.setBeerOrderLines(Set.of(beerOrderLine));

        return beerOrder;
    }
}