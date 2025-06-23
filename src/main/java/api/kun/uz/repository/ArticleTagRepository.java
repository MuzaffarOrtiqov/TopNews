package api.kun.uz.repository;

import api.kun.uz.entity.ArticleTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTagEntity, String> {
    List<ArticleTagEntity> findArticleTagEntitiesByTag_Name(String tagName);
}
