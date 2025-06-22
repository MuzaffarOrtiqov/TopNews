package api.kun.uz.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentFilterDTO {
    //id,created_date_from,created_date_to,profile_id,article_id
    private String id;
    private LocalDateTime createdDateFrom;
    private LocalDateTime createdDateTo;
    private String profileId;
    private String articleId;
}
