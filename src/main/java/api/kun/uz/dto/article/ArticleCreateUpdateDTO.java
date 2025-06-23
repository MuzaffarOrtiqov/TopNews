package api.kun.uz.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleCreateUpdateDTO {
    // title,description,content,imageId, regionId, categoryList[], sectionList[]
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Content is required")
    private String content;
    @NotBlank(message = "Title is required")
    private String imageId;
    @NotBlank(message = "Title is required")
    private String regionId;
    @NotNull(message = "CategoryIds are required")
    private List<String> categoryList;
    @NotNull(message = "SectionIds are required")
    private List<String> sectionList;
    private Integer readTime;
    private List<String> tagNameList;


}
