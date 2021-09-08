package beer.order.service.web.mappers;


import beer.order.service.domain.Beer;
import brewery.model.BeerDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeerModelMapper {


    private final ModelMapper modelMapper;
    private final DateMapper dateMapper;

    @Autowired
    public BeerModelMapper(ModelMapper modelMapper, DateMapper dateMapper) {
        this.modelMapper = modelMapper;
        this.dateMapper = dateMapper;
    }

    public BeerDto convertToDTO(Beer beer) {

        modelMapper.typeMap(Beer.class, BeerDto.class)
                .addMappings(mapper -> {

                    mapper.map(src -> dateMapper.asOffsetDateTime(beer.getCreatedDate()), BeerDto::setCreatedDate);

                    mapper.map(src -> dateMapper.asOffsetDateTime(beer.getLastModifiedDate()), BeerDto::setLastModifiedDate);
                });

        return modelMapper.map(beer, BeerDto.class);
    }

//    BeerDto convertToDtoWithInventory(Beer beer) {
//        BeerDto dto = convertToDTO(beer);
//        dto.setQuantityOnHand(beerInventoryService.getOnhandInventory(beer.getId()));
//        return dto;
//    }

    public Beer convertToEntity(BeerDto beerDto) {

        modelMapper.typeMap(BeerDto.class, Beer.class)
                .addMappings(mapper -> {

                    mapper.map(src -> dateMapper.asTimestamp(beerDto.getCreatedDate()), Beer::setCreatedDate);

                    mapper.map(src -> dateMapper.asTimestamp(beerDto.getLastModifiedDate()), Beer::setLastModifiedDate);
                });

        return modelMapper.map(beerDto, Beer.class);
    }


}
