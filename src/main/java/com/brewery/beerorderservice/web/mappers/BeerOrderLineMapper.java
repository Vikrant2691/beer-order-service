package com.brewery.beerorderservice.web.mappers;


import com.brewery.beerorderservice.domain.BeerOrderLine;
import com.brewery.beerorderservice.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {

    BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
    BeerOrderLine beerOrderDtoLineToBeerOrderLine(BeerOrderLineDto beerOrderLineDto);
}
