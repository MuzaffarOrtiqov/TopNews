package api.kun.uz.dto.saved_article;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedArticleCreateDto {
    @NotBlank(message = "ArticleId is required")
    private String articleId;
}
