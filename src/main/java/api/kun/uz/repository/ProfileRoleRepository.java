package api.kun.uz.repository;

import api.kun.uz.entity.ProfileRoleEntity;
import api.kun.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ProfileRoleEntity as pr WHERE pr.profileId=?1")
    void delete(String profileId);

    @Query("SELECT pr.roles from ProfileRoleEntity as pr WHERE pr.profileId=?1")
    List<ProfileRole> getRoles(String profileId);


    List<ProfileRoleEntity> findProfileRoleByProfileId(String profileId);
}
