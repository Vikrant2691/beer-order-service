package beer.order.service.statemachine;

import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.web.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderStateInterceptor beerOrderStateInterceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {

        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);

        System.out.println("*********** newBeerOrder " + savedBeerOrder.toString());
        return savedBeerOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID orderId, Boolean isValid) {

        BeerOrder beerOrder = beerOrderRepository.findOneById(orderId);
        System.out.println("Validation passed for Order id "+beerOrder.getId().toString());

        if (isValid) {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);
            System.out.println("Validation passed for Order id "+orderId);
//            beerOrder.setOrderStatus(BeerOrderStatusEnum.VALIDATED);
            BeerOrder savedBeerOrder = beerOrderRepository.findOneById(orderId);

            sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
        } else
//            beerOrder.setOrderStatus(BeerOrderStatusEnum.VALIDATED_EXCEPTION);
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);

//        beerOrderRepository.save(beerOrder);
    }


    @Override
    public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findOneById(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
//        beerOrder.setOrderStatus(BeerOrderStatusEnum.ALLOCATED);
        updateAllocatedQty(beerOrderDto, beerOrder);
    }

    @Override
    public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findOneById(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);

//        beerOrder.setOrderStatus(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
        beerOrderRepository.save(beerOrder);
    }

    @Override
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findOneById(beerOrderDto.getId());
        beerOrder.setOrderStatus(BeerOrderStatusEnum.ALLOCATION_PENDING);
        beerOrderRepository.save(beerOrder);
        updateAllocatedQty(beerOrderDto, beerOrder);
    }



    @Override
    public void updateAllocatedQty(BeerOrderDto beerOrderDto, BeerOrder beerOrder) {
        BeerOrder allocatedOrder = beerOrderRepository.findOneById(beerOrderDto.getId());

        allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
            beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
                    beerOrderLine.setOrderQuantity(beerOrderLineDto.getOrderQuantity());
                }
            });
        });

        beerOrderRepository.save(beerOrder);

    }

//    @Transactional
    public void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum beerOrderEventEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);

        Message<BeerOrderEventEnum> msg = MessageBuilder.withPayload(beerOrderEventEnum)
                .setHeader(ORDER_ID_HEADER,beerOrder.getId().toString())
                .build();
        System.out.println("************************Event "+beerOrderEventEnum.toString()+" sent for " + beerOrder.toString());
        sm.sendEvent(msg);
//                .doOnComplete(() -> System.out.println("................HI there")).subscribe();

    }

//    @Transactional
    public StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        System.out.println("--------------------------------------------------i am in builddd");
//        System.out.println("build get state" + sm.getState().getId());
        sm.stop();
        sm.getStateMachineAccessor()
//                .withRegion().addStateMachineInterceptor(beerOrderStateInterceptor);
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(beerOrderStateInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }


}
