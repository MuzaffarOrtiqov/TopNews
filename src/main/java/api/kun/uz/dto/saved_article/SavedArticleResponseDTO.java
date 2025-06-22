package api.kun.uz.dto.saved_article;

import api.kun.uz.dto.article.ArticleShortInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SavedArticleResponseDTO {
    //(id,article(id,title,description,image(id,url)), saved_date)
    private String savedArticleId;
    private ArticleShortInfo articleShortInfo;
    private LocalDateTime savedDate;
}
