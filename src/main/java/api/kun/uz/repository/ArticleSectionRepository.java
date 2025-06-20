package api.kun.uz.repository;

import api.kun.uz.entity.ArticleSectionEntity;
import api.kun.uz.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleSectionRepository extends JpaRepository<ArticleSectionEntity, String> {
    @Query("FROM ArticleSectionEntity WHERE articleId=?1")
    List<ArticleSectionEntity> findArticleSectionByArticleId(String articleId);


}
