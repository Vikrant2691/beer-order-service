package beer.order.service.web.mappers;

import beer.order.service.domain.BeerOrder;
import beer.order.service.web.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

public BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);

public BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto);

}
