package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.category.CategoryCreateDTO;
import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.dto.category.CategoryUpdateDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.entity.CategoryEntity;
import api.kun.uz.entity.RegionEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.mapper.CategoryShortInfoMapper;
import api.kun.uz.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public AppResponse<String> createCategory(CategoryCreateDTO categoryCreateDTO, AppLanguage lang) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setOrderNumber(categoryCreateDTO.getOrderNumber());
        categoryEntity.setNameUz(categoryCreateDTO.getNameUz());
        categoryEntity.setNameRu(categoryCreateDTO.getNameRu());
        categoryEntity.setNameEn(categoryCreateDTO.getNameEn());
        categoryEntity.setKey(categoryCreateDTO.getKey());
        categoryRepository.save(categoryEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("category.created", lang));
    }

    public AppResponse<String> updateCategory(String categoryId, CategoryUpdateDTO categoryUpdateDTO, AppLanguage lang) {
        CategoryEntity categoryEntity = getCategoryById(categoryId,lang);
        categoryEntity.setKey(categoryUpdateDTO.getKey()!=null?categoryUpdateDTO.getKey():categoryEntity.getKey());
        categoryEntity.setNameUz(categoryUpdateDTO.getNameUz()!=null?categoryUpdateDTO.getNameUz():categoryEntity.getNameUz());
        categoryEntity.setNameRu(categoryUpdateDTO.getNameRu()!=null?categoryUpdateDTO.getNameRu():categoryEntity.getNameRu());
        categoryEntity.setNameEn(categoryUpdateDTO.getNameEn()!=null?categoryUpdateDTO.getNameEn():categoryEntity.getNameEn());
        categoryEntity.setOrderNumber(categoryUpdateDTO.getOrderNumber()!=null?categoryUpdateDTO.getOrderNumber():categoryEntity.getOrderNumber());
        categoryRepository.save(categoryEntity);
        return new AppResponse<>(resourceBundleMessageService.getMessage("category.update.success", lang));
    }

    public AppResponse<String> deleteCategory(String categoryId, AppLanguage lang) {
        CategoryEntity categoryEntity = getCategoryById(categoryId,lang);
        categoryRepository.deleteCategory(categoryEntity.getId());
        return new AppResponse<>(resourceBundleMessageService.getMessage("category.delete.success", lang));
    }

    public List<CategoryInfoDTO> getCategories(AppLanguage lang) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findAllCategories();
        return categoryEntityList.stream().map(this::getCategoryFullInfo).toList();
    }

    public List<CategoryShortInfoMapper> getCategoriesByLang(AppLanguage language) {
        return categoryRepository.getCategoriesByLang(language.name());
    }

    // util methods
    public CategoryEntity getCategoryById(String categoryId, AppLanguage lang) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new AppBadException(resourceBundleMessageService.getMessage("category.not.found", lang)));
    }

    private CategoryInfoDTO getCategoryFullInfo (CategoryEntity categoryEntity){
        CategoryInfoDTO dto = new CategoryInfoDTO();
        dto.setId(categoryEntity.getId());
        dto.setNameUz(categoryEntity.getNameUz());
        dto.setNameRu(categoryEntity.getNameRu());
        dto.setNameEn(categoryEntity.getNameEn());
        dto.setKey(categoryEntity.getKey());
        dto.setVisible(categoryEntity.isVisible());
        dto.setCreatedDate(categoryEntity.getCreatedDate());
        return dto;
    }
    public CategoryInfoDTO getCategoryShortInfo (CategoryEntity categoryEntity){
        CategoryInfoDTO dto = new CategoryInfoDTO();
        dto.setId(categoryEntity.getId());
        dto.setNameUz(categoryEntity.getNameUz());
        dto.setNameRu(categoryEntity.getNameRu());
        dto.setNameEn(categoryEntity.getNameEn());
        return dto;
    }
}
