package api.kun.uz.dto.article;

import api.kun.uz.dto.attach.AttachDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleShortInfo {
    // id(uuid),title,description,image(id,url),published_date
    private String articleId;
    private String title;
    private String description;
    private AttachDTO image;
    private LocalDateTime publishedDate;
}
