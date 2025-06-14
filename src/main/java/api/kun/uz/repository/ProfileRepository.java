package api.kun.uz.repository;

import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity  SET status=?2 WHERE id=?1 AND visible=true")
    void updateStatus(String profileId, GeneralStatus generalStatus);

    @Transactional
    @Modifying
    @Query("update ProfileEntity as p set p.password=?1 where p.id=?2")
    void updatePassword(String encodedPassword, String id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity as p set p.name=?1, p.surname=?2 where p.id=?3")
    void updateProfileName(String name, String surname, String userId);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET tempUsername=?1 WHERE id=?2")
    void updateTempUsername(String username, String userId);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET username=?2 WHERE id=?1")
    void updateUsername(String userId, String tempUsername);

    @Query("FROM ProfileEntity AS p ")
    Page<ProfileEntity> findAllProfiles(Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity AS p SET p.visible=false WHERE p.id=?1")
    void deleteProfileById(String profileId);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity p SET p.photoId=?2 WHERE p.id=?1 AND p.visible=true ")
    void updateProfilePhoto(String userId, String photoId);
}
