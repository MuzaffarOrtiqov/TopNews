package api.kun.uz.service;

import api.kun.uz.entity.ProfileRoleEntity;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileRoleService {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void deleteRoles(String profileId) {
        profileRoleRepository.delete(profileId);
    }

    public void create(String profileId, ProfileRole profileRole) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profileId);
        profileRoleEntity.setRoles(profileRole);
        profileRoleRepository.save(profileRoleEntity);
    }

}
