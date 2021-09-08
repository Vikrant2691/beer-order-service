package beer.order.service.repository.beer;


import beer.order.service.domain.Beer;
import brewery.model.BeerEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BeerRepository extends PagingAndSortingRepository<Beer, UUID> {


    Page<Beer> findAllByBeerNameAndBeerStyle(String beerName, BeerEnum beerStyle, PageRequest pageRequest);

    Page<Beer> findAllByBeerName(String beerName, PageRequest pageRequest);

    Page<Beer> findAllByBeerStyle(BeerEnum beerStyle, PageRequest pageRequest);

    Beer findByUpc(String beerUPC);
}
