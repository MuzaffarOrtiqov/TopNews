package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.region.RegionCreateDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.dto.region.RegionUpdateDTO;
import api.kun.uz.entity.RegionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.mapper.RegionShortInfoMapper;
import api.kun.uz.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AppResponse<String> createRegion(RegionCreateDTO regionCreateDTO, AppLanguage lang) {

        RegionEntity regionEntity = new RegionEntity();
        regionEntity.setOrderNumber(regionCreateDTO.getOrderNumber());
        regionEntity.setNameUz(regionCreateDTO.getNameUz());
        regionEntity.setNameRu(regionCreateDTO.getNameRu());
        regionEntity.setNameEn(regionCreateDTO.getNameEn());
        regionEntity.setKey(regionCreateDTO.getKey());
        regionRepository.save(regionEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("region.created", lang));
    }


    public AppResponse<String> updateRegion(String regionId,RegionUpdateDTO regionUpdateDTO, AppLanguage lang) {
      RegionEntity regionEntity = getRegionById(regionId,lang);
      regionEntity.setKey(regionUpdateDTO.getKey()!=null?regionUpdateDTO.getKey():regionEntity.getKey());
      regionEntity.setNameUz(regionUpdateDTO.getNameUz()!=null?regionUpdateDTO.getNameUz():regionEntity.getNameUz());
      regionEntity.setNameRu(regionUpdateDTO.getNameRu()!=null?regionUpdateDTO.getNameRu():regionEntity.getNameRu());
      regionEntity.setNameEn(regionUpdateDTO.getNameEn()!=null?regionUpdateDTO.getNameEn():regionEntity.getNameEn());
      regionRepository.save(regionEntity);
      return new AppResponse<>(resourceBundleMessageService.getMessage("region.update.success", lang));
    }
    public AppResponse<String> deleteRegion(String regionId, AppLanguage lang) {
           RegionEntity regionEntity = getRegionById(regionId,lang);
           regionRepository.deleteRegion(regionEntity.getId());
           return new AppResponse<>(resourceBundleMessageService.getMessage("region.deleted", lang));
    }


    public List<RegionInfoDTO> getRegions(AppLanguage lang) {
        List<RegionEntity> regionEntityList = regionRepository.findAllRegions();
        return regionEntityList.stream().map(this::getRegionFullInfo).toList();
    }

    public List<RegionShortInfoMapper> getRegionsByLang(AppLanguage language) {
      return regionRepository.getRegionsByLang(language.name());
    }


    // util methods
    public RegionEntity getRegionById(String regionId,AppLanguage lang) {
        return regionRepository
                .findById(regionId)
                .orElseThrow(()->new AppBadException(resourceBundleMessageService.getMessage("region.not.found", lang)));
    }

    private RegionInfoDTO getRegionFullInfo (RegionEntity regionEntity){
        RegionInfoDTO dto = new RegionInfoDTO();
        dto.setId(regionEntity.getId());
        dto.setNameUz(regionEntity.getNameUz());
        dto.setNameRu(regionEntity.getNameRu());
        dto.setNameEn(regionEntity.getNameEn());
        dto.setKey(regionEntity.getKey());
        dto.setVisible(regionEntity.isVisible());
        dto.setCreatedDate(regionEntity.getCreatedDate());
        return dto;
    }

}
