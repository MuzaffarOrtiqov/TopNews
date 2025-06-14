package api.kun.uz.dto.profile;

import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileDetailUpdateAdminDTO {
   private String name;
   private String surname;
   private List<ProfileRole> roles;
   private GeneralStatus status;


}
