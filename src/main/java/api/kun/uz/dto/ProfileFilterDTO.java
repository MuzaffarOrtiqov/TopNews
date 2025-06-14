package api.kun.uz.dto;

import api.kun.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProfileFilterDTO {
    //name,surname,phone,role,created_date_from,created_date_to
    private String name;
    private String surname;
    private String username;
    private List<ProfileRole> role; // or List<String> if multiple roles
    private LocalDateTime createdDateFrom;
    private LocalDateTime createdDateTo;
}
