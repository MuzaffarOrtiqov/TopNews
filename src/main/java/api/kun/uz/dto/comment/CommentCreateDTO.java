package api.kun.uz.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    //content,article_id,reply_id
    @NotBlank(message = "Content is required")
    private String content;
    @NotBlank(message = "Article is required")
    private String articleId;
    private String replyId;
}
