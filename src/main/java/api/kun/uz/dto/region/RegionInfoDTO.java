package api.kun.uz.dto.region;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionInfoDTO {
    //id,key,nameUz, nameRu, nameEn, key, visible,created_date
    private String id;
    private String key;
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private boolean visible;
    private LocalDateTime createdDate;

}
