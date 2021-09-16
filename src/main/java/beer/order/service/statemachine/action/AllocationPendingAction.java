package beer.order.service.statemachine.action;


import beer.order.service.domain.BeerOrderEventEnum;
import beer.order.service.domain.BeerOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AllocationPendingAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {


    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        System.out.println("Compensating Action: Allocation Pending");
    }
}
