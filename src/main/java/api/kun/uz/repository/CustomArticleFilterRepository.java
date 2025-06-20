package api.kun.uz.repository;

import api.kun.uz.dto.FilterResultDTO;
import api.kun.uz.dto.article.ArticleFilterModeratorDTO;
import api.kun.uz.dto.article.ArticleFilterPublisherDTO;
import api.kun.uz.dto.article.ArticleFilterUserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;


@Repository
public class CustomArticleFilterRepository {
    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<Object[]> filterArticlesForModerators(Integer page, Integer size, ArticleFilterModeratorDTO articleFilterModeratorDTO, String moderatorId) {

        StringBuilder selectQueryBuilder = new StringBuilder("SELECT a.id, a.title,a.description, a.imageId, a.publishedDate FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true AND a.moderatorId=:moderatorId ");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT (COUNT(a)) FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true AND a.moderatorId=:moderatorId ");

        HashMap<String, Object> params = new HashMap<>();

        if (articleFilterModeratorDTO.getTitle() != null && !articleFilterModeratorDTO.getTitle().isBlank()) {
            selectQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            countQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            params.put("title", "%" + articleFilterModeratorDTO.getTitle().toLowerCase() + "%");
        }

        if (articleFilterModeratorDTO.getRegionId() != null && !articleFilterModeratorDTO.getRegionId().isBlank()) {
            selectQueryBuilder.append(" AND a.regionId =:regionId");
            countQueryBuilder.append(" AND a.regionId =:regionId");
            params.put("regionId", articleFilterModeratorDTO.getRegionId());
        }
        if (articleFilterModeratorDTO.getCategoryId() != null && !articleFilterModeratorDTO.getCategoryId().isBlank()) {
            selectQueryBuilder.append(" AND ace.categoryId =:categoryId");
            countQueryBuilder.append(" AND ace.categoryId =:categoryId");
            params.put("categoryId", articleFilterModeratorDTO.getCategoryId());
        }

        if (articleFilterModeratorDTO.getCreatedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.createdDate >=:createdDateFrom");
            countQueryBuilder.append(" AND a.createdDate >=:createdDateFrom");
            params.put("createdDateFrom", articleFilterModeratorDTO.getCreatedDateFrom());
        }
        if (articleFilterModeratorDTO.getCreatedDateTo() != null) {
            selectQueryBuilder.append(" AND a.createdDate <=:createdDateTo");
            countQueryBuilder.append(" AND a.createdDate <=:createdDateTo");
            params.put("createdDateTo", articleFilterModeratorDTO.getCreatedDateTo());
        }

        if (articleFilterModeratorDTO.getPublishedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            countQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            params.put("publishedDateFrom", articleFilterModeratorDTO.getPublishedDateFrom());
        }
        if (articleFilterModeratorDTO.getPublishedDateTo() != null) {
            selectQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            countQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            params.put("publishedDateTo", articleFilterModeratorDTO.getPublishedDateTo());
        }

        selectQueryBuilder.append(" ORDER BY a.createdDate DESC");

        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());

        params.forEach((key, value) -> {
            selectQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);

        List<Object[]> results = selectQuery.getResultList();
        Long total = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(results, total);

    }

    public FilterResultDTO<Object[]> filterArticlesForUsers(Integer page, Integer size, ArticleFilterUserDTO articleFilterUserDTO) {

        StringBuilder selectQueryBuilder = new StringBuilder("SELECT a.id, a.title,a.description, a.imageId, a.publishedDate FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true AND a.status='PUBLISHED'");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT (COUNT(a)) FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true AND a.status='PUBLISHED'");

        HashMap<String, Object> params = new HashMap<>();

        if (articleFilterUserDTO.getTitle() != null && !articleFilterUserDTO.getTitle().isBlank()) {
            selectQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            countQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            params.put("title", "%" + articleFilterUserDTO.getTitle().toLowerCase() + "%");
        }

        if (articleFilterUserDTO.getRegionId() != null && !articleFilterUserDTO.getRegionId().isBlank()) {
            selectQueryBuilder.append(" AND a.regionId =:regionId");
            countQueryBuilder.append(" AND a.regionId =:regionId");
            params.put("regionId", articleFilterUserDTO.getRegionId());
        }
        if (articleFilterUserDTO.getCategoryId() != null && !articleFilterUserDTO.getCategoryId().isBlank()) {
            selectQueryBuilder.append(" AND ace.categoryId =:categoryId");
            countQueryBuilder.append(" AND ace.categoryId =:categoryId");
            params.put("categoryId", articleFilterUserDTO.getCategoryId());
        }
        if (articleFilterUserDTO.getPublishedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            countQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            params.put("publishedDateFrom", articleFilterUserDTO.getPublishedDateFrom());
        }
        if (articleFilterUserDTO.getPublishedDateTo() != null) {
            selectQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            countQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            params.put("publishedDateTo", articleFilterUserDTO.getPublishedDateTo());
        }

        if (articleFilterUserDTO.getPublishedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            countQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            params.put("publishedDateFrom", articleFilterUserDTO.getPublishedDateFrom());
        }
        if (articleFilterUserDTO.getPublishedDateTo() != null) {
            selectQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            countQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            params.put("publishedDateTo", articleFilterUserDTO.getPublishedDateTo());
        }
        if (articleFilterUserDTO.getStatus() != null && !articleFilterUserDTO.getStatus().name().isBlank()) {
            selectQueryBuilder.append(" AND a.status =:status");
            countQueryBuilder.append(" AND a.status =:status");
            params.put("status", articleFilterUserDTO.getStatus());
        }
        selectQueryBuilder.append(" ORDER BY a.publishedDate DESC");

        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());

        params.forEach((key, value) -> {
            selectQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);

        List<Object[]> results = selectQuery.getResultList();
        Long total = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(results, total);

    }


    public FilterResultDTO<Object[]> filterArticlesForPublishers(Integer page, Integer size, ArticleFilterPublisherDTO articleFilterPublisherDTO) {
        StringBuilder selectQueryBuilder = new StringBuilder("SELECT a.id, a.title,a.description, a.imageId, a.publishedDate FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true ");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT (COUNT(a)) FROM ArticleEntity as a " +
                "INNER JOIN ArticleCategoryEntity AS ace ON ace.articleId=a.id WHERE a.visible=true ");

        HashMap<String, Object> params = new HashMap<>();

        if (articleFilterPublisherDTO.getTitle() != null && !articleFilterPublisherDTO.getTitle().isBlank()) {
            selectQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            countQueryBuilder.append(" AND LOWER(a.title) LIKE :title");
            params.put("title", "%" + articleFilterPublisherDTO.getTitle().toLowerCase() + "%");
        }

        if (articleFilterPublisherDTO.getRegionId() != null && !articleFilterPublisherDTO.getRegionId().isBlank()) {
            selectQueryBuilder.append(" AND a.regionId =:regionId");
            countQueryBuilder.append(" AND a.regionId =:regionId");
            params.put("regionId", articleFilterPublisherDTO.getRegionId());
        }
        if (articleFilterPublisherDTO.getModeratorId() != null && !articleFilterPublisherDTO.getModeratorId().isBlank()) {
            selectQueryBuilder.append(" AND a.moderatorId =:moderatorId");
            countQueryBuilder.append(" AND a.moderatorId =:moderatorId");
            params.put("moderatorId", articleFilterPublisherDTO.getModeratorId());
        }
        if (articleFilterPublisherDTO.getPublisherId() != null && !articleFilterPublisherDTO.getPublisherId().isBlank()) {
            selectQueryBuilder.append(" AND a.publisherId =:publisherId");
            countQueryBuilder.append(" AND a.publisherId =:publisherId");
            params.put("publisherId", articleFilterPublisherDTO.getPublisherId());
        }

        if (articleFilterPublisherDTO.getStatus() != null && !articleFilterPublisherDTO.getStatus().name().isBlank()) {
            selectQueryBuilder.append(" AND a.status =:status");
            countQueryBuilder.append(" AND a.status =:status");
            params.put("status", articleFilterPublisherDTO.getStatus());
        }

        if (articleFilterPublisherDTO.getCategoryId() != null && !articleFilterPublisherDTO.getCategoryId().isBlank()) {
            selectQueryBuilder.append(" AND ace.categoryId =:categoryId");
            countQueryBuilder.append(" AND ace.categoryId =:categoryId");
            params.put("categoryId", articleFilterPublisherDTO.getCategoryId());
        }

        if (articleFilterPublisherDTO.getCreatedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.createdDate >=:createdDateFrom");
            countQueryBuilder.append(" AND a.createdDate >=:createdDateFrom");
            params.put("createdDateFrom", articleFilterPublisherDTO.getCreatedDateFrom());
        }
        if (articleFilterPublisherDTO.getCreatedDateTo() != null) {
            selectQueryBuilder.append(" AND a.createdDate <=:createdDateTo");
            countQueryBuilder.append(" AND a.createdDate <=:createdDateTo");
            params.put("createdDateTo", articleFilterPublisherDTO.getCreatedDateTo());
        }

        if (articleFilterPublisherDTO.getPublishedDateFrom() != null) {
            selectQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            countQueryBuilder.append(" AND a.publishedDate >=:publishedDateFrom");
            params.put("publishedDateFrom", articleFilterPublisherDTO.getPublishedDateFrom());
        }
        if (articleFilterPublisherDTO.getPublishedDateTo() != null) {
            selectQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            countQueryBuilder.append(" AND a.publishedDate <=:publishedDateTo");
            params.put("publishedDateTo", articleFilterPublisherDTO.getPublishedDateTo());
        }

        selectQueryBuilder.append(" ORDER BY a.createdDate DESC");

        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());

        params.forEach((key, value) -> {
            selectQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);

        List<Object[]> results = selectQuery.getResultList();
        Long total = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(results, total);

    }
}
