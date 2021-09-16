package beer.order.service.statemachine;


import beer.order.service.domain.BeerOrder;
import beer.order.service.web.model.BeerOrderDto;

import java.util.UUID;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID orderId, Boolean isValid);

    void beerOrderAllocationPassed(BeerOrderDto beerOrderDto);

    void beerOrderAllocationFailed(BeerOrderDto beerOrderDto);

    void beerOrderPickedUp(UUID id);

    void beerOrderCancelled(UUID id);

    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto);

    void updateAllocatedQty(BeerOrderDto beerOrderDto, BeerOrder beerOrder);
}

