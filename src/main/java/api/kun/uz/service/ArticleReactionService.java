package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.article.ArticleReactionCreateDTO;
import api.kun.uz.entity.ArticleReactionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.repository.ArticleReactionRepository;
import api.kun.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleReactionService {
    @Autowired
    private ArticleReactionRepository articleReactionRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AppResponse<String> leaveReaction(ArticleReactionCreateDTO dto, AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();

        if (profileId == null || profileId.isBlank()) {
            return new AppResponse<>(resourceBundleMessageService.getMessage("not.logged.in", lang));
        }

        // Always use current profile ID
        Optional<ArticleReactionEntity> optional = articleReactionRepository
                .findByArticleIdAndProfileId(dto.getArticleId(), profileId);

        // If reaction exists
        if (optional.isPresent()) {
            ArticleReactionEntity existing = optional.get();

            // If same reaction again → remove (toggle)
            if (existing.getReaction().equals(dto.getReaction())) {
                articleReactionRepository.delete(existing);
                return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.removed", lang));
            } else {
                // Update reaction (e.g. like → dislike)
                articleReactionRepository.updateReaction(profileId, dto.getArticleId(), dto.getReaction());
                return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.updated", lang));
            }

        } else {
            // No reaction before → insert new
            ArticleReactionEntity entity = new ArticleReactionEntity();
            entity.setArticleId(dto.getArticleId());
            entity.setProfileId(profileId);
            entity.setReaction(dto.getReaction());

            articleReactionRepository.save(entity);
            return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.set.success", lang));
        }
    }

}
