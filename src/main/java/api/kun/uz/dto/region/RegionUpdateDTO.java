package api.kun.uz.dto.region;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionUpdateDTO {
    //key,nameUz, nameRu, nameEn, key
    private Integer orderNumber;
    private String key;
    private String nameUz;
    private String nameRu;
    private String nameEn;
}
