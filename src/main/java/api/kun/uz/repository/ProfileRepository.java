package api.kun.uz.repository;

import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
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
    @Query("update ProfileEntity as p set p.password=?2 where p.id=?1")
    void updatePassword(String id, String encodedPassword);
}
