package api.kun.uz.mapper;

import java.time.LocalDateTime;

public interface CommentMapper {

    String getId();

    LocalDateTime getCreatedDate();

    LocalDateTime getUpdatedDate();

    String getProfileId();

    String getContent();

    String getArticleId();

    String getReplyId();

    boolean getVisible();

    Long getLikeCount();

    Long getDislikeCount();
}
