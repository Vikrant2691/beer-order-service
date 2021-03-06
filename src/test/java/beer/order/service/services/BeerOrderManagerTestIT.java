package beer.order.service.services;

import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderLine;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.domain.Customer;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.repository.CustomerRepository;
import beer.order.service.services.beer.BeerServiceImpl;
import beer.order.service.statemachine.BeerOrderManager;
import brewery.model.BeerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.*;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WireMockExtension.class)
@SpringBootTest
class BeerOrderManagerTestIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    Customer testCustomer;

    UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }


    @BeforeEach
    void setUp() {

        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());

    }

    @Test
    void testNewToAllocate() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        System.out.println("testNewToAllocate Beer id "+beerOrder.getId());
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        beerOrderManager.beerOrderPickedUp(beerOrder.getId());

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());

        });
        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
        });

    }

    @Test
    void testFailedValidation() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        beerOrder.setCustomerRef("failed-validation");

        System.out.println("testFailedValidation Beer id "+beerOrder.getId());
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());

        });

    }

    @Test
    void testFailedAllocation() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        beerOrder.setCustomerRef("beerOrderAllocationFailed");

        System.out.println("testFailedValidation Beer id "+beerOrder.getId());
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());

        });

    }

    @Test
    void testPendingAllocation() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        beerOrder.setCustomerRef("beerOrderAllocationPendingInventory");

        System.out.println("testFailedValidation Beer id "+beerOrder.getId());
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());

        });

    }

    @Test
    void validationCancelled() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());

        });

        beerOrderManager.beerOrderCancelled(beerOrder.getId());

        await().untilAsserted(() -> {

            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());

        });

    }


    @Test
    void testStateMachine() {

        BeerOrder beerOrder = beerOrderManager.newBeerOrder(createBeerOrder());
        System.out.println(beerOrder.getOrderStatus());

    }

    public BeerOrder createBeerOrder(){
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());

        beerOrder.setBeerOrderLines(lines);

        return beerOrder;
    }
}