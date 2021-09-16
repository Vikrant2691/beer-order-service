package beer.order.service.services.listener;


import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.OrderValidationResult;
import beer.order.service.statemachine.BeerOrderManager;
import brewery.model.event.FailedAllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailedOrderListener {

    private final BeerOrderManager beerOrderManager;

    @Transactional
    @RabbitListener(queues = {RabbitConfig.FAILED_ALLOCATE_ORDER_QUEUE})
    public void validateResponseListener(final FailedAllocateOrderRequest failedAllocateOrderRequest) {

        final UUID orderId = failedAllocateOrderRequest.getOrderId();
        System.out.println("Order Failed for Order Id :"+orderId);
    }

}
