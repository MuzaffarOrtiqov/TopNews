package api.kun.uz.repository;

import api.kun.uz.entity.RegionEntity;
import api.kun.uz.entity.SectionEntity;
import api.kun.uz.mapper.RegionShortInfoMapper;
import api.kun.uz.mapper.SectionShortInfoMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionRepository extends JpaRepository<SectionEntity, String> {
    @Modifying
    @Transactional
    @Query("update SectionEntity SET visible=false WHERE id=?1 AND visible=true")
    void deleteSection(String id);

    @Query("FROM SectionEntity ORDER BY orderNumber ASC ")
    List<SectionEntity> findAllSections();

    @Query("SELECT s.id as id, s.key as key, " +
            "CASE ?1 " +
            "WHEN 'UZ' THEN s.nameUz " +
            "WHEN 'RU' THEN s.nameRu " +
            "WHEN 'EN' THEN s.nameEn " +
            "END AS name FROM SectionEntity AS s")
    List<SectionShortInfoMapper> getSectionsByLang(String language);

}
