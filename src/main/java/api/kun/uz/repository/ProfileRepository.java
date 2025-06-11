package api.kun.uz.repository;

import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
}
