package beer.order.service.web.mappers;


import beer.order.service.domain.BeerOrder;
import beer.order.service.web.model.BeerOrderDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeerOrderModelMapper {


    private final ModelMapper modelMapper;
    private final DateMapper dateMapper;

    @Autowired
    public BeerOrderModelMapper(ModelMapper modelMapper, DateMapper dateMapper) {
        this.modelMapper = modelMapper;
        this.dateMapper = dateMapper;
    }

    public BeerOrderDto convertToDTO(BeerOrder beerOrder) {

        modelMapper.typeMap(BeerOrder.class, BeerOrderDto.class)
                .addMappings(mapper -> {

                    mapper.map(src -> dateMapper.asOffsetDateTime(beerOrder.getCreatedDate()), BeerOrderDto::setCreatedDate);

                    mapper.map(src -> dateMapper.asOffsetDateTime(beerOrder.getUpdatedDate()), BeerOrderDto::setUpdatedDate);
                    mapper.map(src -> beerOrder.getOrderStatus().toString(), BeerOrderDto::setOrderStatus);
                });

        return modelMapper.map(beerOrder, BeerOrderDto.class);
    }

//    BeerOrderOrderDto convertToDtoWithInventory(BeerOrder beer) {
//        BeerOrderOrderDto dto = convertToDTO(beer);
//        dto.setQuantityOnHand(beerInventoryService.getOnhandInventory(beer.getId()));
//        return dto;
//    }

    public BeerOrder convertToEntity(BeerOrderDto beerDto) {

        modelMapper.typeMap(BeerOrderDto.class, BeerOrder.class)
                .addMappings(mapper -> {

                    mapper.map(src -> dateMapper.asTimestamp(beerDto.getCreatedDate()), BeerOrder::setCreatedDate);

                    mapper.map(src -> dateMapper.asTimestamp(beerDto.getUpdatedDate()), BeerOrder::setUpdatedDate);
                });

        return modelMapper.map(beerDto, BeerOrder.class);
    }


}
