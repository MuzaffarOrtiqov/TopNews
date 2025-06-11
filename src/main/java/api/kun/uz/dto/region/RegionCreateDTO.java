package api.kun.uz.dto.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionCreateDTO {
    @NotNull(message = "OrderNumber is required")
    private  Integer orderNumber;
    @NotBlank(message = "NameUz is required")
    private String nameUz;
    @NotBlank(message = "NameRu is required")
    private String nameRu;
    @NotBlank(message = "NameEn is required")
    private String nameEn;
    @NotBlank(message = "Key is required")
    private String key;


}
