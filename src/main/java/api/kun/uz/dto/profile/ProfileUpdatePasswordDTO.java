package api.kun.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdatePasswordDTO {
    @NotBlank(message = "CurrentPassword is required")
    private String currentPassword;
    @NotBlank(message = "NewPassword is required")
    private String newPassword;
}
