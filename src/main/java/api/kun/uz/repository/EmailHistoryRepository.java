package api.kun.uz.repository;

import api.kun.uz.entity.EmailHistoryEntity;
import api.kun.uz.enums.EmailType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity,String> {
    @Query("SELECT COUNT(eh) FROM EmailHistoryEntity eh WHERE eh.email = ?1 AND eh.emailType = ?2")
    Long getEmailCount(String email, EmailType type);


    Optional<EmailHistoryEntity> findTop1ByEmailOrderByCreatedDateDesc(String username);

    @Transactional
    @Modifying
    @Query("update EmailHistoryEntity as eh SET eh.attemptCount= eh.attemptCount+1 WHERE eh.id=?1")
    void updateAttemptCount(String id);
}
