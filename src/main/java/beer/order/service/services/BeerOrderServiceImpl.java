package beer.order.service.services;

import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.Customer;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.repository.CustomerRepository;
import beer.order.service.web.mappers.BeerOrderMapper;
import beer.order.service.web.model.BeerOrderDto;
import beer.order.service.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    public static String ORDER_ID_HEADER="ORDER_ID_HEADER";

    private final CustomerRepository customerRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    public BeerOrderServiceImpl(CustomerRepository customerRepository, BeerOrderRepository beerOrderRepository, BeerOrderMapper beerOrderMapper) {
        this.customerRepository = customerRepository;
        this.beerOrderRepository = beerOrderRepository;
        this.beerOrderMapper = beerOrderMapper;
    }

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToBeerOrderDto)
                    .collect(Collectors.toList()), PageRequest.of(beerOrderPage.getPageable().getPageNumber(),
                    beerOrderPage.getPageable().getPageSize()), beerOrderPage.getTotalElements());

        } else {
            return null;
        }

    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {

        Optional<Customer> customer = customerRepository.findById(customerId);

        if (customer.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.beerOrderDtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null);
            beerOrder.setCustomer(customer.get());
            beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
            return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
        }

        throw new RuntimeException("Customer not found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
return beerOrderMapper.beerOrderToBeerOrderDto(getOrder(customerId,orderId));

    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {

        BeerOrder beerOrder = getOrder(customerId,orderId);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

            if (beerOrderOptional.isPresent()) {

                BeerOrder beerOrder = beerOrderOptional.get();

                if (beerOrder.getCustomer().getId().equals(customerId)) {
                    return beerOrder;
                }
                throw new RuntimeException("Order not found");
            }
        }
        throw new RuntimeException("Customer not found");

    }



}
