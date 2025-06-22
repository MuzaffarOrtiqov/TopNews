package api.kun.uz.dto.article;

import api.kun.uz.enums.Reaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleReactionCreateDTO {
    @NotBlank(message = "ArticleId is required")
    private String articleId;
    @NotNull(message = "Reaction is required")
    private Reaction reaction;
}
