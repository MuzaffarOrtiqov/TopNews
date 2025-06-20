package api.kun.uz.dto.article;

import api.kun.uz.dto.attach.AttachDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.enums.ArticleStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleInfoDTO {
    private ArticleStatus status;
    private String id;
    private String title;
    private String description;
    private String content;
    private Long sharedCount;
    private AttachDTO image;
    private RegionInfoDTO region;
    private String moderatorId;
    private String publisherId;
    private Integer readTime;
    private LocalDateTime createdDate;
    private LocalDateTime publishedDate;
    private boolean visible;
    private Long viewCount;


}
