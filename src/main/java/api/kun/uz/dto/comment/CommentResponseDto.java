package api.kun.uz.dto.comment;

import api.kun.uz.dto.article.ArticleShortInfo;
import api.kun.uz.dto.profile.ProfileInfoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponseDto {
    //id,created_date,update_date,profile(id,name,surname)
    private String id;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private ProfileInfoDTO profile;
    //id,created_date,update_date,profile(id,name,surname),content,article(id,title),reply_id,
    private String content;
    private ArticleShortInfo article;
    private String reply;
    private String profileId;
    private String articleId;
    private Boolean visible;
    private Long likeCount;
    private Long dislikeCount;


}
