package api.kun.uz.dto.article;

import api.kun.uz.enums.ArticleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleFilterPublisherDTO {
    private String title;
    private String regionId;
    private String categoryId;
    private LocalDateTime createdDateFrom;
    private LocalDateTime createdDateTo;
    private LocalDateTime publishedDateFrom;
    private LocalDateTime publishedDateTo;
    private String moderatorId;
    private String publisherId;
    private ArticleStatus status;
}
