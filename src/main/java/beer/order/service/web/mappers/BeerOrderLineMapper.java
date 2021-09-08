package beer.order.service.web.mappers;


import beer.order.service.domain.BeerOrderLine;
import beer.order.service.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {

    BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
    BeerOrderLine beerOrderDtoLineToBeerOrderLine(BeerOrderLineDto beerOrderLineDto);
}
