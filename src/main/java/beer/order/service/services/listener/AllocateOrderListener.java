package beer.order.service.services.listener;


import beer.order.service.config.RabbitConfig;
import brewery.model.event.AllocateOrderResult;
import beer.order.service.statemachine.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocateOrderListener {

    private final BeerOrderManager beerOrderManager;

    @Transactional
    @RabbitListener(queues = RabbitConfig.ALLOCATE_ORDER_RESULT_QUEUE)
    public void listen(AllocateOrderResult allocateOrderResult){

        UUID beerOrderId= allocateOrderResult.getBeerOrderDto().getId();

        if (!allocateOrderResult.getAllocationError() && !allocateOrderResult.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPassed(allocateOrderResult.getBeerOrderDto());
        }
        if (allocateOrderResult.getAllocationError()) {
            beerOrderManager.beerOrderAllocationFailed(allocateOrderResult.getBeerOrderDto());
        }
        if (!allocateOrderResult.getAllocationError() && allocateOrderResult.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPendingInventory(allocateOrderResult.getBeerOrderDto());
        }

    }

}
