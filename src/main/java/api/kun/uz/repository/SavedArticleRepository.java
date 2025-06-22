package api.kun.uz.repository;


import api.kun.uz.entity.SavedArticleEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SavedArticleRepository extends JpaRepository<SavedArticleEntity, String> {

    boolean existsByArticleIdAndProfileId(String articleId, String profileId);

    @Transactional
    @Modifying
    @Query("UPDATE SavedArticleEntity SET visible=FALSE WHERE id=?1")
    Integer deleteSavedArticle(String savedArticleId);

    @Query("FROM SavedArticleEntity WHERE profileId=?1 AND visible=TRUE ")
    List<SavedArticleEntity> findProfileSavedArticles(String profileId);
}
