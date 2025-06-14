package api.kun.uz.dto.profile;

import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileInfoDTO {
    //name,surname,username(email/phone),password,status, roleList[]
    private String name;
    private String surname;
    private String username;
    private GeneralStatus status;
    private List<ProfileRole> roles;
    private LocalDateTime createdDate;

    public ProfileInfoDTO(String name, String surname, String username) {
        this.name = name;
        this.surname = surname;
        this.username = username;
    }

    public ProfileInfoDTO() {
    }
}

