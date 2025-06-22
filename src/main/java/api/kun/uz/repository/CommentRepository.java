package api.kun.uz.repository;

import api.kun.uz.entity.CommentEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, String> {
    @Transactional
    @Modifying
    @Query("UPDATE CommentEntity SET content=?2, updatedDate= ?3 WHERE id=?1 AND visible=true ")
    void updateComment(String commentId, String content, LocalDateTime updatedDate);

    @Transactional
    @Modifying
    @Query("UPDATE CommentEntity SET visible=false WHERE id=?1 AND visible=true ")
    void deleteComment(String commentId);

    @Query("FROM CommentEntity WHERE articleId=?1 AND visible=TRUE ")
    List<CommentEntity> findCommentsByArticleId(String articleId);

    @Query("FROM CommentEntity WHERE visible=TRUE ")
    Page<CommentEntity> findAllComments(Pageable pageable);

    @Query("FROM CommentEntity c WHERE c.replyId=?1 AND c.visible=TRUE ")
    List<CommentEntity> findAllRepliedComments(String commentId);
}
