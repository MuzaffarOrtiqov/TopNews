package api.kun.uz.dto.section;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionUpdateDTO {
    private Integer orderNumber;
    private String key;
    private String nameUz;
    private String nameRu;
    private String nameEn;
}
