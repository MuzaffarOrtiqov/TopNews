package api.kun.uz.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "article_section")
@Entity
@Getter
@Setter
public class ArticleSectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "article_id")
    private String articleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private ArticleEntity article;

    @Column(name = "section_id")
    private String sectionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private SectionEntity section;
}
