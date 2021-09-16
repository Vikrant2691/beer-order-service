package beer.order.service.services.listener;

import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.OrderValidationResult;
import beer.order.service.web.model.ValidateBeerOrderRequest;
import brewery.model.event.AllocateOrderRequest;
import brewery.model.event.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocateOrderValidator {

    private final RabbitTemplate rabbitTemplate;
    Boolean allocationError = false;
    Boolean pendingInventory = false;

    @Transactional
    @RabbitListener(queues = {RabbitConfig.ALLOCATE_ORDER_QUEUE})
    public void listen(AllocateOrderRequest allocateOrderRequest) {


        if (allocateOrderRequest.getBeerOrderDto().getCustomerRef() != null && allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("beerOrderAllocationFailed")){
            allocationError=true;
            pendingInventory=true;
        }

        if (allocateOrderRequest.getBeerOrderDto().getCustomerRef() != null && allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("beerOrderAllocationPendingInventory")){
            pendingInventory=true;
        }

            System.out.println("######## I RAN in Allocate ##########  " + allocateOrderRequest.toString());
        if (allocateOrderRequest.getBeerOrderDto().getId() != null) {
            System.out.println("######## I RAN TOO in Allocate ##########");

            allocateOrderRequest.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
                beerOrderLineDto.setOrderQuantity(beerOrderLineDto.getQuantityAllocated());
            });

            rabbitTemplate.convertAndSend(
                    RabbitConfig.ALLOCATE_ORDER_RESULT_QUEUE,
                    AllocateOrderResult.builder()
                            .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
                            .allocationError(allocationError)
                            .pendingInventory(pendingInventory)
                            .build());
        }


    }
}
