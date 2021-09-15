package beer.order.service.statemachine.action;

import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.services.BeerOrderServiceImpl;
import beer.order.service.web.mappers.BeerOrderMapper;
import beer.order.service.web.mappers.BeerOrderModelMapper;
import beer.order.service.web.model.ValidateBeerOrderRequest;
import brewery.model.event.AllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final RabbitTemplate rabbitTemplate;
//    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderModelMapper beerOrderModelMapper;


    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {

        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderServiceImpl.ORDER_ID_HEADER);

        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(Objects.requireNonNull(beerOrderId)));

        System.out.println(beerOrder.toString());
        rabbitTemplate.convertAndSend(RabbitConfig.ALLOCATE_ORDER_QUEUE, AllocateOrderRequest.builder()
        .beerOrderDto(beerOrderModelMapper.convertToDTO(beerOrder)).build());

        System.out.println("Sent Allocation request for beerId: "+beerOrderId);

    }
}
