package api.kun.uz.dto.profile;

import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileCreateDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Surname is required")
    private String surname;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @NotNull(message = "Status is required")
    private GeneralStatus status;
    @NotNull(message = "Role(s) is/are required")
    private List<ProfileRole> roleList;
}
