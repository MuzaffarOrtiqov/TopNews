package api.kun.uz.service;

import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.dto.section.SectionInfoDTO;
import api.kun.uz.entity.ArticleCategoryEntity;
import api.kun.uz.entity.ArticleSectionEntity;
import api.kun.uz.entity.CategoryEntity;
import api.kun.uz.entity.SectionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ArticleSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleSectionService {
    @Autowired
    private ArticleSectionRepository articleSectionRepository;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public void setArticleSection(String id, List<String> sectionIdList, AppLanguage lang) {
        sectionIdList.forEach(sectionId -> {
            sectionService.getSectionById(sectionId, lang); //check if sections exits
            ArticleSectionEntity articleSectionEntity = new ArticleSectionEntity();
            articleSectionEntity.setArticleId(id);
            articleSectionEntity.setSectionId(sectionId);
            articleSectionRepository.save(articleSectionEntity);
        });
    }

    public void updateArticleSection(String articleId, List<String> sectionIdList, AppLanguage lang) {
        List<ArticleSectionEntity> articleSectionEntityList = getArticleSectionList(articleId, lang);
        int i = 0;
        // 1. Update existing ArticleSection entities with new Sections
        for (; i < articleSectionEntityList.size() && i < sectionIdList.size(); i++) {
            articleSectionEntityList.get(i).setSectionId(sectionIdList.get(i));
        }
        // 2. If sectionIdList were fewer than existing entities, delete the extras
        if (articleSectionEntityList.size() > sectionIdList.size()) {
            List<ArticleSectionEntity> toRemove = articleSectionEntityList.subList(sectionIdList.size(), articleSectionEntityList.size());
            articleSectionRepository.deleteAll(toRemove);
        }
        // 3. If sectionIdList were more, create new ArticleSectionEntity (optional, for completeness)
        for (; i < sectionIdList.size(); i++) {
            ArticleSectionEntity newEntity = new ArticleSectionEntity();
            newEntity.setArticleId(articleId);
            newEntity.setSectionId(sectionIdList.get(i));
            articleSectionRepository.save(newEntity); // Replace with actual save logic
        }

    }

    private List<ArticleSectionEntity> getArticleSectionList(String articleId, AppLanguage lang) {
        List<ArticleSectionEntity> articleSectionEntityList = articleSectionRepository.findArticleSectionByArticleId(articleId);
        if (articleSectionEntityList.isEmpty()) {
            throw new AppBadException(resourceBundleMessageService.getMessage("no.item.found", lang) + articleId);
        }
        return articleSectionEntityList;
    }

    public List<SectionInfoDTO> getSectionInfoList(String articleId, AppLanguage lang) {
        List<ArticleSectionEntity> articleSectionEntityList = articleSectionRepository.findArticleSectionByArticleId(articleId);
        List<SectionEntity> sectionEntityList = new LinkedList<>();
        articleSectionEntityList.forEach(articleSectionEntity -> {
            sectionEntityList.add(sectionService.getSectionById(articleSectionEntity.getSectionId(), lang));
        });
        return sectionEntityList.stream().map(categoryEntity -> sectionService.getSectionShortInfo(categoryEntity)).collect(Collectors.toList());
    }


}
