package api.kun.uz.repository;

import api.kun.uz.entity.ArticleCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategoryEntity, String> {
    @Query("FROM ArticleCategoryEntity WHERE articleId=?1")
    List<ArticleCategoryEntity> findArticleCategoryByArticleId(String articleId);
}
