package api.kun.uz.repository;

import api.kun.uz.entity.SmsHistoryEntity;
import api.kun.uz.enums.SmsType;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, String> {

    Long countByPhoneAndCreatedDateBetween(String phoneNumber, LocalDateTime localDateTime, LocalDateTime now);

    Optional<SmsHistoryEntity> findTop1ByPhoneOrderByCreatedDateDesc(String phone);

    @Modifying
    @Transactional
    @Query("update SmsHistoryEntity as sh set sh.attemptCount=sh.attemptCount+1 WHERE sh.id=?1")
    void updateAttemptCount(String id);

    @Query("FROM SmsHistoryEntity WHERE phone=?1 ORDER BY createdDate DESC ")
    List<SmsHistoryEntity> findByPhone(String phoneNumber);

    @Query("FROM SmsHistoryEntity WHERE createdDate BETWEEN ?1 AND ?2")
    List<SmsHistoryEntity> findByDate(LocalDateTime startDate, LocalDateTime endDate);

    @Query("FROM SmsHistoryEntity ")
    Page<SmsHistoryEntity> findAllHistories(Pageable pageable);
}
