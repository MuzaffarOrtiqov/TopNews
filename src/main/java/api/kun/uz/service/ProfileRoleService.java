package api.kun.uz.service;

import api.kun.uz.entity.ProfileRoleEntity;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileRoleService {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void deleteRoles(String profileId) {
        profileRoleRepository.delete(profileId);
    }

    public void create(String profileId, List<ProfileRole> profileRoles) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profileId);
        profileRoles.forEach(profileRoleEntity::setRoles);
        profileRoleRepository.save(profileRoleEntity);
    }

}
