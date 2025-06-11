package api.kun.uz.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateDTO {
    private Integer orderNumber;
    private String key;
    private String nameUz;
    private String nameRu;
    private String nameEn;
}
