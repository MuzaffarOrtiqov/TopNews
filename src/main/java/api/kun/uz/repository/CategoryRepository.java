package api.kun.uz.repository;

import api.kun.uz.entity.CategoryEntity;
import api.kun.uz.entity.RegionEntity;
import api.kun.uz.mapper.CategoryShortInfoMapper;
import api.kun.uz.mapper.RegionShortInfoMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    @Modifying
    @Transactional
    @Query("update CategoryEntity SET visible=false WHERE id=?1 AND visible=true")
    void deleteCategory(String id);

    @Query("FROM CategoryEntity ORDER BY orderNumber ASC ")
    List<CategoryEntity> findAllCategories();

    @Query("SELECT c.id as id, c.key as key, " +
            "CASE ?1 " +
            "WHEN 'UZ' THEN c.nameUz " +
            "WHEN 'RU' THEN c.nameRu " +
            "WHEN 'EN' THEN c.nameEn " +
            "END AS name FROM CategoryEntity AS c")
    List<CategoryShortInfoMapper> getCategoriesByLang(String language);
}
