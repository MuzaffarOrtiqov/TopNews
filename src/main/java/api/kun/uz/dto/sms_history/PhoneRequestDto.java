package api.kun.uz.dto.sms_history;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneRequestDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
