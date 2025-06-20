package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.dto.section.SectionCreateDTO;
import api.kun.uz.dto.section.SectionInfoDTO;
import api.kun.uz.dto.section.SectionUpdateDTO;
import api.kun.uz.entity.CategoryEntity;
import api.kun.uz.entity.RegionEntity;
import api.kun.uz.entity.SectionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.mapper.SectionShortInfoMapper;
import api.kun.uz.repository.SectionRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AppResponse<String> createSection(SectionCreateDTO sectionCreateDTO, AppLanguage lang) {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setOrderNumber(sectionCreateDTO.getOrderNumber());
        sectionEntity.setNameUz(sectionCreateDTO.getNameUz());
        sectionEntity.setNameRu(sectionCreateDTO.getNameRu());
        sectionEntity.setNameEn(sectionCreateDTO.getNameEn());
        sectionEntity.setKey(sectionCreateDTO.getKey());
        sectionRepository.save(sectionEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("section.create.success", lang));
    }

    public AppResponse<String> updateSection(String sectionId, SectionUpdateDTO sectionUpdateDTO, AppLanguage lang) {
        SectionEntity sectionEntity = getSectionById(sectionId, lang);
        sectionEntity.setKey(sectionUpdateDTO.getKey() != null ? sectionUpdateDTO.getKey() : sectionEntity.getKey());
        sectionEntity.setNameUz(sectionUpdateDTO.getNameUz() != null ? sectionUpdateDTO.getNameUz() : sectionEntity.getNameUz());
        sectionEntity.setNameRu(sectionUpdateDTO.getNameRu() != null ? sectionUpdateDTO.getNameRu() : sectionEntity.getNameRu());
        sectionEntity.setNameEn(sectionUpdateDTO.getNameEn() != null ? sectionUpdateDTO.getNameEn() : sectionEntity.getNameEn());
        sectionEntity.setOrderNumber(sectionUpdateDTO.getOrderNumber() != null ? sectionUpdateDTO.getOrderNumber() : sectionEntity.getOrderNumber());
        sectionRepository.save(sectionEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("section.update.success", lang));
    }

    public AppResponse<String> deleteSection(String sectionId, AppLanguage lang) {
        SectionEntity sectionEntity = getSectionById(sectionId, lang);
        sectionRepository.deleteSection(sectionEntity.getId());
        return new AppResponse<>(resourceBundleMessageService.getMessage("section.delete.success", lang));
    }

    public List<SectionInfoDTO> getSections(AppLanguage lang) {
        List<SectionEntity> sectionEntityList = sectionRepository.findAllSections();
        return sectionEntityList.stream().map(this::getSectionFullInfo).toList();
    }

    public List<SectionShortInfoMapper> getSectionsByLang(AppLanguage language) {
        return sectionRepository.getSectionsByLang(language.name());
    }

    // util methods
    public SectionEntity getSectionById(String sectionId, AppLanguage lang) {
        return sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("section.not.found", lang)));


    }

    private SectionInfoDTO getSectionFullInfo(SectionEntity sectionEntity) {
        SectionInfoDTO dto = new SectionInfoDTO();
        dto.setId(sectionEntity.getId());
        dto.setNameUz(sectionEntity.getNameUz());
        dto.setNameRu(sectionEntity.getNameRu());
        dto.setNameEn(sectionEntity.getNameEn());
        dto.setKey(sectionEntity.getKey());
        dto.setVisible(sectionEntity.isVisible());
        dto.setCreatedDate(sectionEntity.getCreatedDate());
        return dto;
    }

    public SectionInfoDTO getSectionShortInfo(SectionEntity sectionEntity) {
        SectionInfoDTO dto = new SectionInfoDTO();
        dto.setId(sectionEntity.getId());
        dto.setNameUz(sectionEntity.getNameUz());
        dto.setNameRu(sectionEntity.getNameRu());
        dto.setNameEn(sectionEntity.getNameEn());
        return dto;
    }


}
