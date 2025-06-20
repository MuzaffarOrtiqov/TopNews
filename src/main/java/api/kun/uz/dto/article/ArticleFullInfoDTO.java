package api.kun.uz.dto.article;

import api.kun.uz.dto.attach.AttachDTO;
import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.dto.section.SectionInfoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleFullInfoDTO {
    private String id;
    private String title;
    private String description;
    private String content;
    private Long sharedCount;
    private RegionInfoDTO region;
    private AttachDTO image;
    private List<CategoryInfoDTO> category;
    private List<SectionInfoDTO> section;
    private LocalDateTime publishedDate;
    private Long viewCount;
  //  private Long likeCount;
    //TODO ADD TAG LIST AND LIKE COUNT
    // private List<>

}
