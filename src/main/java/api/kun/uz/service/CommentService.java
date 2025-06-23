package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.comment.CommentCreateDTO;
import api.kun.uz.dto.comment.CommentFilterDTO;
import api.kun.uz.dto.comment.CommentResponseDto;
import api.kun.uz.dto.comment.CommentUpdateDTO;
import api.kun.uz.entity.CommentEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.CommentRepository;
import api.kun.uz.repository.CustomCommentFilterRepository;
import api.kun.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CustomCommentFilterRepository commentFilterRepository;
    @Autowired
    private TelegramNotificationService  telegramNotificationService;


    public AppResponse<String> createComment(CommentCreateDTO commentCreateDTO, AppLanguage lang) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(commentCreateDTO.getContent());
        commentEntity.setArticleId(commentCreateDTO.getArticleId());
        commentEntity.setReplyId(commentCreateDTO.getReplyId());
        commentEntity.setProfileId(SpringSecurityUtil.getCurrentProfileId());
        commentRepository.save(commentEntity);
        telegramNotificationService.sendCommentToGroup(articleService.getArticleById(commentEntity.getArticleId(),lang).getTitle(), SpringSecurityUtil.getCurrentProfile().getUsername(),commentEntity.getContent());
        return new AppResponse<>(resourceBundleMessageService.getMessage("comment.create.success", lang));
    }

    public AppResponse<String> updateComment(String commentId, CommentUpdateDTO commentUpdateDTO, AppLanguage lang) {
        CommentEntity comment = getComment(commentId, lang);
        if (!isOwnerOrAdmin(comment, lang)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("comment.update.error", lang));
        }
        commentRepository.updateComment(commentId, commentUpdateDTO.getContent(), LocalDateTime.now());
        return new AppResponse<>(resourceBundleMessageService.getMessage("comment.update.success", lang));
    }

    public AppResponse<String> deleteComment(String commentId, AppLanguage lang) {
        CommentEntity comment = getComment(commentId, lang);
        if (!isOwner(comment)) {
            throw new AppBadException(resourceBundleMessageService.getMessage("comment.delete.error", lang));
        }
        commentRepository.deleteComment(commentId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("comment.delete.success", lang));
    }

    public CommentResponseDto getCommentDetails(String commentId, AppLanguage lang) {
        CommentEntity comment = getComment(commentId, lang);
        return toCommentFullDto(comment, lang);
    }

    public Page<CommentResponseDto> getComments(Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<CommentEntity> commentEntityPage = commentRepository.findAllComments(pageable);
        List<CommentResponseDto> commentResponseDtoList = commentEntityPage.stream().map(commentEntity -> toCommentDetailedDto(commentEntity, lang)).toList();
        return new PageImpl<>(commentResponseDtoList, pageable, commentEntityPage.getTotalElements());
    }

    public Page<CommentResponseDto> filter(Integer page, Integer size, CommentFilterDTO commentFilterDTO, AppLanguage lang) {
        Page<CommentEntity> commentEntityPage = commentFilterRepository.filter(page, size, commentFilterDTO);
        List<CommentResponseDto> commentResponseDtoList = commentEntityPage
                .stream()
                .map(commentEntity -> toCommentFullDto(commentEntity, lang))
                .toList();
        return new PageImpl<>(commentResponseDtoList, PageRequest.of(page, size), commentEntityPage.getTotalElements());
    }

    public List<CommentResponseDto> getAllCommentReplies(String commentId, AppLanguage lang) {
        List<CommentEntity> commentEntityList = commentRepository.findAllRepliedComments(commentId);
        return commentEntityList.stream().map(commentEntity -> toCommentSummaryDto(commentEntity, lang)).toList();
    }

    public List<CommentResponseDto> getArticleComments(String articleId, AppLanguage lang) {
        List<CommentEntity> commentEntityList = commentRepository.findCommentsByArticleId(articleId);
        return commentEntityList.stream().map(commentEntity -> toCommentForArticleDto(commentEntity, lang)).toList();

    }

    //util

    private CommentEntity getComment(String commentId, AppLanguage lang) {
        return commentRepository.findById(commentId).orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("comment.not.found", lang)));
    }

    private boolean isOwnerOrAdmin(CommentEntity comment, AppLanguage lang) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (comment.getProfileId().equals(profileId)) {
            return true;
        }
        List<ProfileRole> profileRoleList = SpringSecurityUtil.getCurrentProfile().getRoleList();
        for (ProfileRole profileRole : profileRoleList) {
            if (profileRole == ProfileRole.ROLE_ADMIN) {
                return true;
            }
        }
        return false;
    }

    private boolean isOwner(CommentEntity comment) {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (comment.getProfileId().equals(profileId)) {
            return true;
        }
        return false;
    }

    private CommentResponseDto toCommentSummaryDto(CommentEntity commentEntity, AppLanguage lang) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(commentEntity.getId());
        commentResponseDto.setCreatedDate(commentEntity.getCreatedDate());
        commentResponseDto.setUpdatedDate(commentEntity.getUpdatedDate());
        commentResponseDto.setProfile(profileService.toProfileShortInfo(commentEntity.getProfileId(), lang));
        commentResponseDto.setVisible(null);
        return commentResponseDto;
    }

    private CommentResponseDto toCommentDetailedDto(CommentEntity commentEntity, AppLanguage lang) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(commentEntity.getId());
        commentResponseDto.setCreatedDate(commentEntity.getCreatedDate());
        commentResponseDto.setUpdatedDate(commentEntity.getUpdatedDate());
        commentResponseDto.setProfile(profileService.toProfileShortInfo(commentEntity.getProfileId(), lang));
        commentResponseDto.setContent(commentEntity.getContent());
        commentResponseDto.setArticle(articleService.toShortInfoDTO(commentEntity.getArticleId(), lang));
        commentResponseDto.setReply(commentEntity.getReplyId());
        return commentResponseDto;
    }

    private CommentResponseDto toCommentFullDto(CommentEntity commentEntity, AppLanguage lang) {
        //    id,created_date,update_date,profile_id,content,article_id,reply_id,visible, like_count, dislike_count
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(commentEntity.getId());
        commentResponseDto.setCreatedDate(commentEntity.getCreatedDate());
        commentResponseDto.setUpdatedDate(commentEntity.getUpdatedDate());
        commentResponseDto.setProfileId(commentEntity.getProfileId());
        commentResponseDto.setContent(commentEntity.getContent());
        commentResponseDto.setArticleId(commentEntity.getArticleId());
        commentResponseDto.setReply(commentEntity.getReplyId());
        commentResponseDto.setVisible(commentEntity.isVisible());
        commentResponseDto.setLikeCount(commentEntity.getLikeCount());
        commentResponseDto.setDislikeCount(commentEntity.getDislikeCount());
        return commentResponseDto;
    }

    private CommentResponseDto toCommentForArticleDto(CommentEntity commentEntity, AppLanguage lang) {
        //   {id,created_date,update_date,profile(id,name,image(id,url)),content,article_id, likeCount, DislikeCount
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(commentEntity.getId());
        commentResponseDto.setCreatedDate(commentEntity.getCreatedDate());
        commentResponseDto.setUpdatedDate(commentEntity.getUpdatedDate());
        commentResponseDto.setProfile(profileService.toProfileShortInfo(commentEntity.getProfileId(), lang));
        commentResponseDto.setContent(commentEntity.getContent());
        commentResponseDto.setArticleId(commentEntity.getArticleId());
        commentResponseDto.setLikeCount(commentEntity.getLikeCount());
        commentResponseDto.setDislikeCount(commentEntity.getDislikeCount());
        return commentResponseDto;
    }


}

