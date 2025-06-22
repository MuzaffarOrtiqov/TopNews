package api.kun.uz.repository;

import api.kun.uz.dto.comment.CommentFilterDTO;
import api.kun.uz.entity.CommentEntity;
import api.kun.uz.mapper.CommentMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class CustomCommentFilterRepository {
    @Autowired
    private EntityManager entityManager;
    public Page<CommentEntity> filter(Integer page, Integer size, CommentFilterDTO commentFilterDTO) {
        StringBuilder selectQueryBuilder = new StringBuilder("SELECT c FROM CommentEntity c WHERE 1=1");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT count(c) FROM CommentEntity c WHERE 1=1");

        HashMap<String, Object> params = new HashMap<>();

        if (commentFilterDTO.getId() != null && !commentFilterDTO.getId().isBlank()) {
            selectQueryBuilder.append(" AND c.id =:id");
            countQueryBuilder.append(" AND c.id =:id");
            params.put("id", commentFilterDTO.getId());
        }
        if (commentFilterDTO.getCreatedDateFrom() != null) {
            selectQueryBuilder.append(" AND c.createdDate >=:createdDateFrom ");
            countQueryBuilder.append(" AND c.createdDate >=:createdDateFrom ");
            params.put("createdDateFrom", commentFilterDTO.getCreatedDateFrom());
        }
        if (commentFilterDTO.getCreatedDateTo() != null) {
            selectQueryBuilder.append(" AND c.createdDate <=:createdDateTo ");
            countQueryBuilder.append(" AND c.createdDate <=:createdDateTo ");
            params.put("createdDateTo", commentFilterDTO.getCreatedDateTo());
        }
        if (commentFilterDTO.getProfileId() != null && !commentFilterDTO.getProfileId().isBlank()) {
            selectQueryBuilder.append(" AND c.profileId =:profileId");
            countQueryBuilder.append(" AND c.profileId =:profileId");
            params.put("profileId", commentFilterDTO.getProfileId());
        }
        if (commentFilterDTO.getArticleId() != null && !commentFilterDTO.getArticleId().isBlank()) {
            selectQueryBuilder.append(" AND c.articleId =:articleId");
            countQueryBuilder.append(" AND c.articleId =:articleId");
            params.put("articleId", commentFilterDTO.getArticleId());
        }

        selectQueryBuilder.append(" ORDER BY c.createdDate DESC");

        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());

        params.forEach((k, v) -> {
            selectQuery.setParameter(k, v);
            countQuery.setParameter(k, v);
        });

        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);

        List<CommentEntity> result = selectQuery.getResultList();
        Long count = (Long) countQuery.getSingleResult();
        return new PageImpl<CommentEntity>(result, PageRequest.of(page, size), count);

    }
}
