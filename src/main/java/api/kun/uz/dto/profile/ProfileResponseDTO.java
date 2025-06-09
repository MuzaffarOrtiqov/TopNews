package api.kun.uz.dto.profile;

import api.kun.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {
    private String id;
    private String name;
    private String username;
    private List<ProfileRole> roleList;
    private String jwt;
    //private AttachDTO attachDTO;
}
