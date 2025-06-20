package api.kun.uz.repository;

import api.kun.uz.entity.ArticleEntity;
import api.kun.uz.enums.ArticleStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {
    @Transactional
    @Modifying
    @Query("UPDATE ArticleEntity SET visible=false WHERE id=?1")
    void deleteArticle(String articleId);


    @Query("FROM ArticleEntity AS a INNER JOIN ArticleSectionEntity ase ON a.id=ase.articleId WHERE ase.sectionId=?1 AND a.visible=true")
    Page<ArticleEntity> findAllArticlesWithSameSectionId(String sectionId, Pageable pageable);

    @Query(
            value = "SELECT * FROM article WHERE id NOT IN (:ids) AND status = 'PUBLISHED' ORDER BY created_date DESC",
            countQuery = "SELECT count(*) FROM article WHERE id NOT IN (:ids) AND status = 'PUBLISHED'",
            nativeQuery = true
    )
    Page<ArticleEntity> findPublishedArticlesExcludingIds(
            @Param("ids") List<String> articleIdList,
            Pageable pageable
    );

    @Query("FROM ArticleEntity AS a INNER JOIN ArticleCategoryEntity ace ON a.id=ace.articleId WHERE ace.categoryId=?1 AND a.visible=true")
    Page<ArticleEntity> findAllArticlesOnSameCategory(String categoryId, Pageable pageable);

    @Query("FROM ArticleEntity AS a WHERE a.regionId=?1 AND a.visible=true")
    Page<ArticleEntity> findAllArticlesOnSameRegion(String regionId, Pageable pageable);


    @Query("FROM ArticleEntity AS a INNER JOIN ArticleSectionEntity ase ON a.id=ase.articleId " +
            "WHERE a.id NOT IN ?1 AND ase.sectionId=?2 AND a.visible=true ORDER BY a.createdDate DESC ")
    List<ArticleEntity> getLatest4ArticleOnSameSectionExcludingArticleId(String articleId, String sectionId, Pageable pageable);

    @Query("FROM ArticleEntity AS a WHERE a.id NOT IN ?1 AND a.visible =TRUE ORDER BY a.viewCount DESC ")
    List<ArticleEntity> getMostReadFourArticlesExcludingCurrentArticle(String articleId);

    @Transactional
    @Modifying
    @Query("UPDATE ArticleEntity SET viewCount=viewCount+1 WHERE id=?1 AND visible=TRUE AND status='PUBLISHED'")
    Integer incrementViewCount(String articleId);

    @Transactional
    @Modifying
    @Query("UPDATE ArticleEntity SET sharedCount=COALESCE(sharedCount,0) +1 WHERE id=?1 AND visible=TRUE AND status='PUBLISHED'")
    Integer incrementShareCount(String articleId);
}
