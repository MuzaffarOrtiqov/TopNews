package api.kun.uz.repository;

import api.kun.uz.entity.TagEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, String> {
    @Query("FROM TagEntity WHERE name=?1 AND visible= TRUE ")
    Optional<TagEntity> findByName(String tagName);
}
