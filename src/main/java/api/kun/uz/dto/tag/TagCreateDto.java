package api.kun.uz.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagCreateDto {
    @NotBlank(message = "TagName is required")
    private String tagName;
}
