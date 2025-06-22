package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.saved_article.SavedArticleCreateDto;
import api.kun.uz.dto.saved_article.SavedArticleResponseDTO;
import api.kun.uz.entity.ArticleEntity;
import api.kun.uz.entity.SavedArticleEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.SavedArticleRepository;
import api.kun.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedArticleService {
    @Autowired
    private SavedArticleRepository savedArticleRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private ArticleService articleService;

    public AppResponse<String> createSavedArticle(SavedArticleCreateDto savedArticleCreateDto, AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (savedArticleRepository.existsByArticleIdAndProfileId(savedArticleCreateDto.getArticleId(), profileId)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("cannot.save.again", lang));
        }
        SavedArticleEntity savedArticleEntity = new SavedArticleEntity();
        savedArticleEntity.setArticleId(savedArticleCreateDto.getArticleId());
        savedArticleEntity.setProfileId(profileId);
        savedArticleRepository.save(savedArticleEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("article.save.success", lang));
    }

    public AppResponse<String> deleteSavedArticle(String savedArticleId, AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (!isOwner(profileId, savedArticleId, lang)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("no.right.delete", lang));
        }
        Integer line = savedArticleRepository.deleteSavedArticle(savedArticleId);
        if (line != 1) {
            throw new AppBadException(resourceBundleMessageService.getMessage("article.delete.error", lang));
        }
        return new AppResponse<>(resourceBundleMessageService.getMessage("article.delete.success", lang));
    }

    //util

    private SavedArticleEntity getSavedArticle(String articleId, AppLanguage lang) {
        return savedArticleRepository.findById(articleId).orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("saved.article.not.found", lang)));
    }

    private boolean isOwner(String profileId, String articleId, AppLanguage lang) {
        SavedArticleEntity savedArticleEntity = getSavedArticle(articleId, lang);
        return savedArticleEntity.getProfileId().equals(profileId);
    }

    public List<SavedArticleResponseDTO> getProfileSavedArticles(AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        List<SavedArticleEntity> savedArticleEntityList = savedArticleRepository.findProfileSavedArticles(profileId);
        return savedArticleEntityList.stream().map(savedArticleEntity -> toSavedArticleResponseDTO(savedArticleEntity,lang)).toList();
    }

    private SavedArticleResponseDTO toSavedArticleResponseDTO(SavedArticleEntity savedArticleEntity, AppLanguage lang) {
        SavedArticleResponseDTO dto = new SavedArticleResponseDTO();
        dto.setSavedArticleId(savedArticleEntity.getArticleId());
        dto.setArticleShortInfo(articleService.toDetailedInfo(savedArticleEntity.getArticleId(), lang));
        dto.setSavedDate(savedArticleEntity.getCreatedDate());
        return dto;
    }
}
