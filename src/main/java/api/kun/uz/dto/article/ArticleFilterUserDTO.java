package api.kun.uz.dto.article;

import api.kun.uz.enums.ArticleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleFilterUserDTO {
    //title,region_id,category_id,published_date_from,published_date_to
    private String title;
    private String regionId;
    private String categoryId;
    private LocalDateTime publishedDateFrom;
    private LocalDateTime publishedDateTo;
    private ArticleStatus status;

}
