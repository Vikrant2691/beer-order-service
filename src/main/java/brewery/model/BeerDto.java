package brewery.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {


    @Null
    private UUID id;

    @NotBlank
    private String beerName;

    @NotNull
    private BeerEnum beerStyle;

    @NotNull
    private String upc;

    @Positive
    private Integer quantityOnHand;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Positive
    @NotNull
    private BigDecimal price;

    @Null
    private Integer version;

    @Null
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime createdDate;

    @Null
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime lastModifiedDate;

    @JsonSerialize(using = LocalDateSerialization.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate localDate;
}
