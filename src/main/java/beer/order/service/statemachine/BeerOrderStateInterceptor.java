package beer.order.service.statemachine;

import beer.order.service.domain.BeerOrder;
import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import beer.order.service.repository.BeerOrderRepository;
import beer.order.service.services.BeerOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class  BeerOrderStateInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
//    @Transactional
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine,

                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> rootStateMachine) {
        System.out.println("-----------------------------in interceptor "+state.getId());

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    System.out.println("Saving state for order id: " + orderId + " Status: " + state.getId());

                    BeerOrder beerOrder = beerOrderRepository.getById(UUID.fromString(orderId));
                    System.out.println("Beer order: " + beerOrder.toString());
                    beerOrder.setOrderStatus(state.getId());
                    beerOrderRepository.saveAndFlush(beerOrder);
                });

    }
}
