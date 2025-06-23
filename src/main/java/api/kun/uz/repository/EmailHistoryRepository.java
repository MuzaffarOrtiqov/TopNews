package api.kun.uz.repository;

import api.kun.uz.entity.EmailHistoryEntity;
import api.kun.uz.enums.EmailType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity,String> {
    @Query("SELECT COUNT(eh) FROM EmailHistoryEntity eh WHERE eh.email = ?1 AND eh.emailType = ?2")
    Long getEmailCount(String email, EmailType type);


    Optional<EmailHistoryEntity> findTop1ByEmailOrderByCreatedDateDesc(String username);

    @Transactional
    @Modifying
    @Query("update EmailHistoryEntity as eh SET eh.attemptCount= eh.attemptCount+1 WHERE eh.id=?1")
    void updateAttemptCount(String id);

    @Query("FROM EmailHistoryEntity WHERE email=?1")
    List<EmailHistoryEntity> findEmailHistory(String email);

    @Query("FROM EmailHistoryEntity WHERE createdDate BETWEEN ?1 AND ?2")
    List<EmailHistoryEntity> findByDate(LocalDateTime startDate, LocalDateTime date);

    @Query("FROM EmailHistoryEntity ")
    Page<EmailHistoryEntity> findAllHistories(Pageable pageable);
}
