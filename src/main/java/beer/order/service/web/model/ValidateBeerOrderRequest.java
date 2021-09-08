package beer.order.service.web.model;

import beer.order.service.domain.BeerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateBeerOrderRequest {

    BeerOrderDto beerOrderDto;

}
