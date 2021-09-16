package beer.order.service.statemachine;


import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocateOrderAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> deallocateOrderAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validationFailedAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocationFailedAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocationPendingAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {

        states.withStates()
                .initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTIONS)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.CANCELLED)
                .end(BeerOrderStatusEnum.PICKED_UP);

    }


    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {

        transitions
                .withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING)
                .event(BeerOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
                .and()
                .withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATED)
                .event(BeerOrderEventEnum.VALIDATION_PASSED)
                .and()
                .withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(BeerOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailedAction)
                .and()
                .withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
                .and()
                .withExternal().source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING)
                .event(BeerOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
                .and()
                .withExternal().source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
                .and()
                .withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATED)
                .event(BeerOrderEventEnum.ALLOCATION_SUCCESS)
                .and()
                .withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(BeerOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailedAction)
                .and()
                .withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
                .and()
                .withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.PENDING_INVENTORY)
                .event(BeerOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .action(allocationPendingAction)
                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.PICKED_UP)
                .event(BeerOrderEventEnum.BEERORDER_PICKED_UP)
                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
                .action(deallocateOrderAction);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> config) throws Exception {
        StateMachineListenerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<BeerOrderStatusEnum, BeerOrderEventEnum> from, State<BeerOrderStatusEnum, BeerOrderEventEnum> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration()
                .listener(adapter);
    }


}
