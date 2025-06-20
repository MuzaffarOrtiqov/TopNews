package api.kun.uz.entity;

import api.kun.uz.enums.ArticleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "article")
public class ArticleEntity {
    /* id(uuid),title,description,content,shared_count,image_id,
    region_id,moderator_id,publisher_id,status(Published,NotPublished),
    readTime (maqolani nechi daqiqa o'qilishi)
    created_date,published_date,visible,view_count*/
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "content", columnDefinition = "text")
    private String content;
    @Column(name = "shared_count")
    private Long sharedCount=0l;

    @Column(name = "image_id")
    private String imageId;
    @JoinColumn(name = "image_id",insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AttachEntity image;

    @Column(name = "region_id")
    private String regionId;
    @JoinColumn(name = "region_id",insertable = false, updatable = false)
    @OneToOne (fetch = FetchType.LAZY)
    private RegionEntity region;

    @Column(name = "moderator_id")
    private String moderatorId;
    @JoinColumn(name = "moderator_id",insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity moderator;

    @Column(name = "publisher_id")
    private String publisherId;
    @JoinColumn(name = "publisher_id",insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity publisher;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ArticleStatus status=ArticleStatus.NOT_PUBLISHED;

    @Column(name = "read_time")
    private Integer readTime;

    @Column(name = "created_date")
    private LocalDateTime createdDate=LocalDateTime.now();

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "visible")
    private boolean visible=true;

    @Column(name = "view_count")
    private Long viewCount=0l;











}
