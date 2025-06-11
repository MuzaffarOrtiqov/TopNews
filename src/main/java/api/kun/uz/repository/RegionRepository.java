package api.kun.uz.repository;

import api.kun.uz.entity.RegionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.mapper.RegionShortInfoMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RegionRepository extends JpaRepository<RegionEntity, String> {
    @Modifying
    @Transactional
    @Query("update RegionEntity SET visible=false WHERE id=?1 AND visible=true")
    void deleteRegion(String id);

    @Query("FROM RegionEntity ORDER BY orderNumber ASC ")
    List<RegionEntity> findAllRegions();

    @Query("SELECT r.id as id, r.key as key, " +
            "CASE ?1 " +
            "WHEN 'UZ' THEN r.nameUz " +
            "WHEN 'RU' THEN r.nameRu " +
            "WHEN 'EN' THEN r.nameEn " +
            "END AS name FROM RegionEntity AS r")
    List<RegionShortInfoMapper> getRegionsByLang(String language);
}
