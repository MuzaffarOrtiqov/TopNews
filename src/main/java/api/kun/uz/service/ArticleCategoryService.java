package api.kun.uz.service;

import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.entity.ArticleCategoryEntity;
import api.kun.uz.entity.CategoryEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ArticleCategoryRepository;
import api.kun.uz.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleCategoryService {
    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public void setArticleCategory(String id, List<String> categoryIdList, AppLanguage lang) {
        categoryIdList.forEach(categoryId -> {
            categoryService.getCategoryById(categoryId, lang); //check if categories exist
            ArticleCategoryEntity articleCategoryEntity = new ArticleCategoryEntity();
            articleCategoryEntity.setArticleId(id);
            articleCategoryEntity.setCategoryId(categoryId);
            articleCategoryRepository.save(articleCategoryEntity);
        });
    }

    private List<ArticleCategoryEntity> getArticleCategoryList(String articleId, AppLanguage lang) {
        List<ArticleCategoryEntity> articleCategoryEntityList = articleCategoryRepository.findArticleCategoryByArticleId(articleId);
        if (articleCategoryEntityList.isEmpty()) {
            throw new AppBadException("No item found for articleId: " + articleId);
        }
        return articleCategoryEntityList;
    }


    public void updateArticleCategory(String articleId, List<String> categoryIdList, AppLanguage lang) {
        List<ArticleCategoryEntity> articleCategoryEntityList = getArticleCategoryList(articleId, lang);
        int i = 0;
        // 1. Update existing ArticleCategory entities with new Categories
        for (; i < articleCategoryEntityList.size() && i < categoryIdList.size(); i++) {
            articleCategoryEntityList.get(i).setCategoryId(categoryIdList.get(i));
        }
        // 2. If categoryIdList were fewer than existing entities, delete the extras
        if (articleCategoryEntityList.size() > categoryIdList.size()) {
            List<ArticleCategoryEntity> toRemove = articleCategoryEntityList.subList(categoryIdList.size(), articleCategoryEntityList.size());
            articleCategoryRepository.deleteAll(toRemove);
        }
        // 3. If categoryIdList were more, create new ArticleCategoryEntity (optional, for completeness)
        for (; i < categoryIdList.size(); i++) {
            ArticleCategoryEntity newEntity = new ArticleCategoryEntity();
            newEntity.setArticleId(articleId);
            newEntity.setCategoryId(categoryIdList.get(i));
            articleCategoryRepository.save(newEntity); // Replace with actual save logic
        }

    }

    public List<CategoryInfoDTO> getCategoryInfoList(String articleId, AppLanguage lang) {
        List<ArticleCategoryEntity> articleCategoryEntityList = articleCategoryRepository.findArticleCategoryByArticleId(articleId);
        List<CategoryEntity> categoryEntityList = new LinkedList<>();
        articleCategoryEntityList.forEach(articleCategoryEntity -> {
            categoryEntityList.add(categoryService.getCategoryById(articleCategoryEntity.getCategoryId(), lang));
        });
        return categoryEntityList.stream().map(categoryEntity -> categoryService.getCategoryShortInfo(categoryEntity)).collect(Collectors.toList());
    }


}
