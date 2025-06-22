package api.kun.uz.repository;

import api.kun.uz.entity.ArticleReactionEntity;
import api.kun.uz.enums.Reaction;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleReactionRepository extends JpaRepository<ArticleReactionEntity, String> {
    @Query("FROM ArticleReactionEntity as are WHERE are.articleId=?1 AND are.profileId=?2")
    Optional<ArticleReactionEntity> findByArticleIdAndProfileId(String articleId, String profileId);

    @Transactional
    @Modifying
    @Query("UPDATE ArticleReactionEntity AS are SET are.reaction=?3 WHERE are.profileId=?1 AND are.articleId=?2")
    void updateReaction(String profileId, String articleId,Reaction reaction);
}
