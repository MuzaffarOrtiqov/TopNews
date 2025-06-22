package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.comment.CommentReactionCreateDto;
import api.kun.uz.entity.ArticleReactionEntity;
import api.kun.uz.entity.CommentReactionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.repository.CommentReactionRepository;
import api.kun.uz.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CommentReactionService {
    @Autowired
    private CommentReactionRepository commentReactionRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AppResponse<String> leaveReaction(CommentReactionCreateDto dto, AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (profileId == null || profileId.isBlank()) {
            return new AppResponse<>(resourceBundleMessageService.getMessage("not.logged.in", lang));
        }

        // Always use current profile ID
        Optional<CommentReactionEntity> optional = commentReactionRepository
                .findByCommentIdAndProfileId(dto.getCommentId(), profileId);

        // If reaction exists
        if (optional.isPresent()) {
            CommentReactionEntity existing = optional.get();

            // If same reaction again → remove (toggle)
            if (existing.getReaction().equals(dto.getReaction())) {
                commentReactionRepository.delete(existing);
                return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.removed", lang));
            } else {
                // Update reaction (e.g. like → dislike)
                commentReactionRepository.updateReaction(profileId, dto.getCommentId(), dto.getReaction());
                return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.updated", lang));
            }

        } else {
            // No reaction before → insert new
            CommentReactionEntity entity = new CommentReactionEntity();
            entity.setCommentId(dto.getCommentId());
            entity.setProfileId(profileId);
            entity.setReaction(dto.getReaction());

            commentReactionRepository.save(entity);
            return new AppResponse<>(resourceBundleMessageService.getMessage("reaction.set.success", lang));
        }
    }
}
