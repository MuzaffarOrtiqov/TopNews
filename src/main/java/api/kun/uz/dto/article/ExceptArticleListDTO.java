package api.kun.uz.dto.article;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExceptArticleListDTO {
    @NotNull(message = "ArticleIdList is required")
    private List<String> articleIdList;
}
