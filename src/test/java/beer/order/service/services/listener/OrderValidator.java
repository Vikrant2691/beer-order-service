package beer.order.service.services.listener;

import beer.order.service.config.RabbitConfig;
import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.OrderValidationResult;
import beer.order.service.web.model.BeerOrderDto;
import beer.order.service.web.model.ValidateBeerOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValidator {

    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = {RabbitConfig.VALIDATE_ORDER_QUEUE})
    public void listen(ValidateBeerOrderRequest validateBeerOrderRequest) {



        System.out.println("######## I RAN ##########  "+ validateBeerOrderRequest.toString());
        if(validateBeerOrderRequest.getBeerOrderDto().getId()!=null) {
            System.out.println("######## I RAN TOO##########");
            rabbitTemplate.convertAndSend(
                    RabbitConfig.VALIDATE_ORDER_RESULT_QUEUE,
                    OrderValidationResult.builder()
                            .orderId(validateBeerOrderRequest.getBeerOrderDto().getId())
                            .isValid(true)
                            .build());
        }


    }
}
