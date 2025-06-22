package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.FilterResultDTO;
import api.kun.uz.dto.article.*;
import api.kun.uz.entity.ArticleEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.ArticleStatus;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ArticleRepository;
import api.kun.uz.repository.CustomArticleFilterRepository;
import api.kun.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleSectionService articleSectionService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private CustomArticleFilterRepository customArticleFilterRepository;

    public ArticleInfoDTO createArticle(ArticleCreateUpdateDTO articleCreateUpdateDTO, AppLanguage lang) {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(articleCreateUpdateDTO.getTitle());
        articleEntity.setDescription(articleCreateUpdateDTO.getDescription());
        articleEntity.setContent(articleCreateUpdateDTO.getContent());
        articleEntity.setImageId(articleCreateUpdateDTO.getImageId());
        articleEntity.setRegionId(articleCreateUpdateDTO.getRegionId());
        articleEntity.setReadTime(articleCreateUpdateDTO.getReadTime());
        articleEntity.setModeratorId(SpringSecurityUtil.getCurrentProfileId());
        articleRepository.save(articleEntity);
        articleCategoryService.setArticleCategory(articleEntity.getId(), articleCreateUpdateDTO.getCategoryList(), lang);
        articleSectionService.setArticleSection(articleEntity.getId(), articleCreateUpdateDTO.getSectionList(), lang);
        return toArticleInfoDTO(articleEntity, lang);
    }

    public ArticleInfoDTO updateArticle(String articleId, ArticleCreateUpdateDTO articleCreateUpdateDTO, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        articleEntity.setTitle(articleCreateUpdateDTO.getTitle());
        articleEntity.setDescription(articleCreateUpdateDTO.getDescription());
        articleEntity.setContent(articleCreateUpdateDTO.getContent());
        articleEntity.setReadTime(articleCreateUpdateDTO.getReadTime());
        String oldImageId = articleEntity.getImageId();
        String newImageId = articleCreateUpdateDTO.getImageId();
        // Update the article with the new image first
        if (newImageId != null && !newImageId.equals(oldImageId)) {
            articleEntity.setImageId(newImageId);
            articleRepository.save(articleEntity); // Update the foreign key in the DB
            // Now safe to delete old image
            attachService.delete(oldImageId, lang);
        }
        articleEntity.setRegionId(articleCreateUpdateDTO.getRegionId());
        articleRepository.save(articleEntity);
        articleCategoryService.updateArticleCategory(articleEntity.getId(), articleCreateUpdateDTO.getCategoryList(), lang);
        articleSectionService.updateArticleSection(articleEntity.getId(), articleCreateUpdateDTO.getSectionList(), lang);
        return toArticleInfoDTO(articleEntity, lang);
    }

    public AppResponse<String> deleteArticle(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        articleRepository.deleteArticle(articleId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("article.delete.success", lang));
    }

    public AppResponse<String> updateArticleStatus(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        //get Publisher id
        articleEntity.setPublisherId(SpringSecurityUtil.getCurrentProfileId());
        if (articleEntity.getStatus().equals(ArticleStatus.NOT_PUBLISHED)) {
            articleEntity.setPublishedDate(LocalDateTime.now());
            articleEntity.setStatus(ArticleStatus.PUBLISHED);
        } else if (articleEntity.getStatus().equals(ArticleStatus.PUBLISHED)) {
            articleEntity.setStatus(ArticleStatus.NOT_PUBLISHED);
        }
        articleRepository.save(articleEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("article.update.status", lang));
    }

    public Page<ArticleShortInfo> getArticlesBySectionId(String sectionId, Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<ArticleEntity> articleEntityPage = articleRepository.findAllArticlesWithSameSectionId(sectionId, pageable);
        List<ArticleShortInfo> articleShortInfoList = articleEntityPage.stream().map(articleEntity -> toShortInfoDTO(articleEntity, lang)).toList();
        return new PageImpl<>(articleShortInfoList, pageable, articleEntityPage.getTotalElements());
    }

    public Page<ArticleShortInfo> getArticlesExceptGivenArticleIdList(ExceptArticleListDTO exceptArticleListDTO, Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "published_date"));
        Page<ArticleEntity> articleEntityPage;
        if (exceptArticleListDTO.getArticleIdList() == null || exceptArticleListDTO.getArticleIdList().isEmpty()) {
            articleEntityPage = articleRepository.findAll(pageable);
        }
        articleEntityPage = articleRepository.findPublishedArticlesExcludingIds(exceptArticleListDTO.getArticleIdList(), pageable);
        List<ArticleShortInfo> articleShortInfoList = articleEntityPage.stream().map(articleEntity -> toShortInfoDTO(articleEntity, lang)).toList();
        return new PageImpl<>(articleShortInfoList, pageable, articleEntityPage.getTotalElements());
    }

    public Page<ArticleShortInfo> getArticlesByCategoryId(String categoryId, Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<ArticleEntity> articleEntityPage = articleRepository.findAllArticlesOnSameCategory(categoryId, pageable);
        List<ArticleShortInfo> articleShortInfoList = articleEntityPage.stream().map(articleEntity -> toShortInfoDTO(articleEntity, lang)).toList();
        return new PageImpl<>(articleShortInfoList, pageable, articleEntityPage.getTotalElements());
    }

    public Page<ArticleFullInfoDTO> getArticlesByRegionId(String regionId, Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<ArticleEntity> articleEntityPage = articleRepository.findAllArticlesOnSameRegion(regionId, pageable);
        List<ArticleFullInfoDTO> articleShortInfoList = articleEntityPage
                .stream()
                .map(articleEntity -> toArticleFullInfoDTO(articleEntity, lang))
                .toList();
        return new PageImpl<>(articleShortInfoList, pageable, articleEntityPage.getTotalElements());
    }

    public ArticleFullInfoDTO getArticleFullInfo(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        return toArticleFullInfoDTO(articleEntity, lang);
    }

    public List<ArticleShortInfo> getLatest4ArticleOnSameSectionExcludingArticleId(String articleId, ArticleRequestSectionDTO dto, AppLanguage lang) {
        Pageable pageable = PageRequest.of(0, 4);
        List<ArticleEntity> articleEntityList = articleRepository.getLatest4ArticleOnSameSectionExcludingArticleId(articleId, dto.getSectionId(), pageable);
        return articleEntityList.stream().map(articleEntity -> toShortInfoDTO(articleEntity, lang)).toList();
    }

    public List<ArticleShortInfo> getMostReadFourArticlesExcludingCurrentArticle(String articleId, AppLanguage lang) {
        List<ArticleEntity> articleEntityList = articleRepository.getMostReadFourArticlesExcludingCurrentArticle(articleId);
        return articleEntityList.stream().map(articleEntity -> toShortInfoDTO(articleEntity, lang)).toList();
    }

    public AppResponse<String> incrementViewCount(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        Integer changedLines = articleRepository.incrementViewCount(articleId);
        return new AppResponse<>(changedLines == 1 ? resourceBundleMessageService.getMessage("view.count.increased", lang) : resourceBundleMessageService.getMessage("view.count.not.changed", lang));
    }

    public AppResponse<String> incrementShareCount(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        Integer changedLines = articleRepository.incrementShareCount(articleId);
        return new AppResponse<>(changedLines == 1 ? resourceBundleMessageService.getMessage("share.count.increased", lang) : resourceBundleMessageService.getMessage("share.count.not.changed", lang));
    }

    public Page<ArticleShortInfo> filterArticlesForUsers(Integer page, Integer size, ArticleFilterUserDTO articleFilterUserDTO, AppLanguage lang) {
        FilterResultDTO<Object[]> result = customArticleFilterRepository.filterArticlesForUsers(page, size, articleFilterUserDTO);
        List<ArticleShortInfo> articleShortInfoList = result
                .getList()
                .stream()
                .map(this::toShortInfoDTO)
                .toList();
        return new PageImpl<>(articleShortInfoList, PageRequest.of(page, size), result.getCount());
    }

    public Page<ArticleShortInfo> filterArticlesForModerators(Integer page, Integer size, ArticleFilterModeratorDTO articleFilterModeratorDTO, AppLanguage lang) {
        String moderatorId = SpringSecurityUtil.getCurrentProfileId();
        FilterResultDTO<Object[]> result = customArticleFilterRepository.filterArticlesForModerators(page, size, articleFilterModeratorDTO, moderatorId);
        List<ArticleShortInfo> articleShortInfoList = result
                .getList()
                .stream()
                .map(this::toShortInfoDTO)
                .toList();
        return new PageImpl<>(articleShortInfoList, PageRequest.of(page, size), result.getCount());
    }

    public Page<ArticleShortInfo> filterArticlesForPublishers(Integer page, Integer size, ArticleFilterPublisherDTO articleFilterPublisherDTO, AppLanguage lang) {
        FilterResultDTO<Object[]> result = customArticleFilterRepository.filterArticlesForPublishers(page, size, articleFilterPublisherDTO);
        List<ArticleShortInfo> articleShortInfoList = result
                .getList()
                .stream()
                .map(this::toShortInfoDTO)
                .toList();
        return new PageImpl<>(articleShortInfoList, PageRequest.of(page, size), result.getCount());
    }

    //util methods

    public ArticleInfoDTO toArticleInfoDTO(ArticleEntity articleEntity, AppLanguage lang) {
        ArticleInfoDTO articleInfoDTO = new ArticleInfoDTO();
        articleInfoDTO.setId(articleEntity.getId());
        articleInfoDTO.setTitle(articleEntity.getTitle());
        articleInfoDTO.setDescription(articleEntity.getDescription());
        articleInfoDTO.setContent(articleEntity.getContent());
        articleInfoDTO.setImage(attachService.attachShortInfo(articleEntity.getImageId()));
        articleInfoDTO.setRegion(regionService.getRegionShortInfo(articleEntity.getRegionId(), lang));
        articleInfoDTO.setCreatedDate(articleEntity.getCreatedDate());
        articleInfoDTO.setReadTime(articleEntity.getReadTime());
        articleInfoDTO.setViewCount(articleEntity.getViewCount());
        return articleInfoDTO;
    }

    public ArticleFullInfoDTO toArticleFullInfoDTO(ArticleEntity articleEntity, AppLanguage lang) {
        ArticleFullInfoDTO articleFullInfoDTO = new ArticleFullInfoDTO();
        articleFullInfoDTO.setId(articleEntity.getId());
        articleFullInfoDTO.setTitle(articleEntity.getTitle());
        articleFullInfoDTO.setDescription(articleEntity.getDescription());
        articleFullInfoDTO.setContent(articleEntity.getContent());
        articleFullInfoDTO.setSharedCount(articleEntity.getSharedCount());
        articleFullInfoDTO.setRegion(regionService.getRegionShortInfo(articleEntity.getRegionId(), lang));
        articleFullInfoDTO.setImage(attachService.attachShortInfo(articleEntity.getImageId()));
        articleFullInfoDTO.setCategory(articleCategoryService.getCategoryInfoList(articleEntity.getId(), lang));
        articleFullInfoDTO.setSection(articleSectionService.getSectionInfoList(articleEntity.getId(), lang));
        articleFullInfoDTO.setPublishedDate(articleEntity.getPublishedDate());
        articleFullInfoDTO.setViewCount(articleEntity.getViewCount());
        return articleFullInfoDTO;
    }

    private ArticleShortInfo toShortInfoDTO(ArticleEntity articleEntity, AppLanguage lang) {
        ArticleShortInfo articleShortInfo = new ArticleShortInfo();
        articleShortInfo.setArticleId(articleEntity.getId());
        articleShortInfo.setTitle(articleEntity.getTitle());
        articleShortInfo.setDescription(articleEntity.getDescription());
        articleShortInfo.setImage(attachService.attachShortInfo(articleEntity.getImageId()));
        articleShortInfo.setPublishedDate(articleEntity.getPublishedDate());
        return articleShortInfo;
    }

    private ArticleShortInfo toShortInfoDTO(Object[] obj) {
        ArticleShortInfo articleShortInfo = new ArticleShortInfo();
        articleShortInfo.setArticleId(obj[0].toString());
        articleShortInfo.setTitle(obj[1].toString());
        articleShortInfo.setDescription(obj[2].toString());
        articleShortInfo.setImage(attachService.attachShortInfo(obj[3].toString()));
        articleShortInfo.setPublishedDate((LocalDateTime) obj[4]);
        return articleShortInfo;
    }

    public ArticleShortInfo toShortInfoDTO(String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        ArticleShortInfo articleShortInfo = new ArticleShortInfo();
        articleShortInfo.setArticleId(articleId);
        articleShortInfo.setTitle(articleEntity.getTitle());
        return articleShortInfo;
    }

    public ArticleShortInfo toDetailedInfo (String articleId, AppLanguage lang) {
        ArticleEntity articleEntity = getArticleById(articleId, lang);
        ArticleShortInfo articleShortInfo = new ArticleShortInfo();
        articleShortInfo.setArticleId(articleId);
        articleShortInfo.setTitle(articleEntity.getTitle());
        articleShortInfo.setDescription(articleEntity.getDescription());
        articleShortInfo.setImage(attachService.attachShortInfo(articleEntity.getImageId()));
        return articleShortInfo;
    }


    private ArticleEntity getArticleById(String articleId, AppLanguage lang) {
        return articleRepository
                .findById(articleId)
                .orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("article.not.found", lang)));

    }


}
