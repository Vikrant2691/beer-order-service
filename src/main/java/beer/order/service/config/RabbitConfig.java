package beer.order.service.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String BREWING_REQUEST_QUEUE = "brewing-request";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order-queue";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order-queue";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";
    public static final String ALLOCATE_ORDER_RESULT_QUEUE = "allocate-order-result";
    public static final String ALLOCATE_ORDER_REQUEST_QUEUE = "allocate-order-request";
    public static final String FAILED_ALLOCATE_ORDER_QUEUE = "failed_allocate_order";
    public static final String DEALLOCATE_ORDER_QUEUE = "deallocate-order";

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

//    @Bean
//    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public Queue brewingQ() {
        return new Queue(BREWING_REQUEST_QUEUE);
    }

    @Bean
    public Queue inventoryQ() {
        return new Queue(NEW_INVENTORY_QUEUE);
    }

    @Bean
    public Queue validateOrderQ() {
        return new Queue(VALIDATE_ORDER_QUEUE);
    }

    @Bean
    public Queue allocateOrderQ() {
        return new Queue(ALLOCATE_ORDER_QUEUE);
    }

    @Bean
    public Queue validateOrderResultQ() {
        return new Queue(VALIDATE_ORDER_RESULT_QUEUE);
    }

    @Bean
    public Queue allocateOrderRequest() {
        return new Queue(ALLOCATE_ORDER_REQUEST_QUEUE);
    }

    @Bean
    public Queue allocateOrderResultQ() {
        return new Queue(ALLOCATE_ORDER_RESULT_QUEUE);
    }

    @Bean
    public Queue deallocateOrderResultQ() {
        return new Queue(DEALLOCATE_ORDER_QUEUE);
    }

    @Bean
    public Queue failedAllocateOrderQ() {
        return new Queue(FAILED_ALLOCATE_ORDER_QUEUE);
    }

    @Bean
    public Module dateTimeModule() {
        return new JavaTimeModule();
    }
}
