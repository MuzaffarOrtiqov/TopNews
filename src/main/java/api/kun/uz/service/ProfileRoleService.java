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
        profileRoles.forEach(profileRole -> {
            ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
            profileRoleEntity.setProfileId(profileId);
            profileRoleEntity.setRoles(profileRole);
            profileRoleRepository.save(profileRoleEntity);
        });
    }

    public List<ProfileRole> getRoles(String profileId) {
        return profileRoleRepository.getRoles(profileId);
    }

    public void updateRoles(String profileId, List<ProfileRole> roles) {

        List<ProfileRoleEntity> profileRoleEntityList = findProfileRoleByProfileId(profileId);

        int i = 0;
        // 1. Update existing profile-role entities with new roles
        for (; i < profileRoleEntityList.size() && i < roles.size(); i++) {
            profileRoleEntityList.get(i).setRoles(roles.get(i));
        }

        // 2. If roles were fewer than existing entities, delete the extras
        if (profileRoleEntityList.size() > roles.size()) {
            List<ProfileRoleEntity> toRemove = profileRoleEntityList.subList(roles.size(), profileRoleEntityList.size());
            toRemove.forEach(entity -> profileRoleRepository.delete(entity)); // Replace with actual delete logic
        }

        // 3. If roles were more, create new ProfileRoleEntities (optional, for completeness)
        for (; i < roles.size(); i++) {
            ProfileRoleEntity newEntity = new ProfileRoleEntity();
            newEntity.setProfileId(profileId);
            newEntity.setRoles(roles.get(i));
            profileRoleRepository.save(newEntity); // Replace with actual save logic
        }

    }

    private List<ProfileRoleEntity> findProfileRoleByProfileId(String profileId) {
        return profileRoleRepository.findProfileRoleByProfileId(profileId);

    }


}
