package api.kun.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeConfirmDTO {
    @NotBlank(message = "Code is required")
    private String code;
}
