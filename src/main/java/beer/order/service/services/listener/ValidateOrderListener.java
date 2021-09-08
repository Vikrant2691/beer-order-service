package beer.order.service.services.listener;


import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.OrderValidationResult;
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
public class ValidateOrderListener {

    private final BeerOrderManager beerOrderManager;

    @RabbitListener(queues = {RabbitConfig.VALIDATE_ORDER_RESULT_QUEUE})
    public void validateResponseListener(final OrderValidationResult orderValidationResult) {

        final UUID orderId= orderValidationResult.getOrderId();

        System.out.println("Validation result of order id is: "+orderId);

        beerOrderManager.processValidationResult(orderId,orderValidationResult.getIsValid());

    }

    }
