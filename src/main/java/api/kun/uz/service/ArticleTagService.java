package api.kun.uz.service;

import api.kun.uz.entity.ArticleTagEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.repository.ArticleTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleTagService {
    @Autowired
    private ArticleTagRepository articleTagRepository;
    @Autowired
    private TagService tagService;

    public void setArticleTag(String articleId, List<String> tagName, String profileId, AppLanguage lang) {
        List<String> tagIdList = tagService.createTag(tagName, profileId, lang);
        tagIdList.forEach(tagId -> {
            ArticleTagEntity articleTagEntity = new ArticleTagEntity();
            articleTagEntity.setArticleId(articleId);
            articleTagEntity.setTagId(tagId);
            articleTagRepository.save(articleTagEntity);
        });
    }

    public List<ArticleTagEntity> getArticlesByTagName(String tagName, AppLanguage lang) {
        List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findArticleTagEntitiesByTag_Name(tagName);
        return articleTagEntityList;
    }
}
