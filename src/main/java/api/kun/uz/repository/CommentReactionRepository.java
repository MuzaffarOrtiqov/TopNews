package api.kun.uz.repository;


import api.kun.uz.entity.ArticleReactionEntity;
import api.kun.uz.entity.CommentReactionEntity;
import api.kun.uz.enums.Reaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReactionEntity, String> {

    @Query("FROM CommentReactionEntity cre WHERE cre.commentId=?1 AND cre.profileId=?2 ")
    Optional<CommentReactionEntity> findByCommentIdAndProfileId(String commentId, String profileId);

    @Transactional
    @Modifying
    @Query("UPDATE CommentReactionEntity SET reaction=?3 WHERE profileId=?1 AND commentId=?2 ")
    void updateReaction(String profileId, String commentId, Reaction reaction);
}
