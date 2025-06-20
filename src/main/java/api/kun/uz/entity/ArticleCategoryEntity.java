package api.kun.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "article_category")
@Entity
@Getter
@Setter
public class ArticleCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "article_id")
    private String articleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",insertable = false, updatable = false)
    private ArticleEntity article;

    @Column(name = "category_id")
    private String categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",insertable = false, updatable = false)
    private CategoryEntity category;

}
