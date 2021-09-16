package beer.order.service.statemachine.action;


import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.services.BeerOrderServiceImpl;
import beer.order.service.web.mappers.BeerOrderModelMapper;
import brewery.model.event.AllocateOrderRequest;
import brewery.model.event.FailedAllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationFailedAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final BeerOrderModelMapper beerOrderModelMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {

        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderServiceImpl.ORDER_ID_HEADER);

        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(Objects.requireNonNull(beerOrderId)));

        System.out.println(beerOrder.toString());
        rabbitTemplate.convertAndSend(RabbitConfig.FAILED_ALLOCATE_ORDER_QUEUE, FailedAllocateOrderRequest.builder()
                .orderId(beerOrder.getId()).build());

        System.out.println("Sent Allocation request for beerId: "+beerOrderId);

        System.out.println("Compensating Action: Allocation Failed");
    }
}
