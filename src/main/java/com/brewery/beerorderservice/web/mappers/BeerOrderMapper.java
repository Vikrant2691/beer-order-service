package com.brewery.beerorderservice.web.mappers;

import com.brewery.beerorderservice.domain.BeerOrder;
import com.brewery.beerorderservice.web.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

public BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);

public BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto);

}
