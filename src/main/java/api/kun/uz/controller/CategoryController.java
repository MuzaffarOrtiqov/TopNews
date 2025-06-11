package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.category.CategoryCreateDTO;
import api.kun.uz.dto.category.CategoryInfoDTO;
import api.kun.uz.dto.category.CategoryUpdateDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.mapper.CategoryShortInfoMapper;
import api.kun.uz.service.CategoryService;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jdk.jfr.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category/")
@Tag(name = "CategoryController", description = "A set of APIs to work with category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add new category", description = "Method used to create new category")
    public ResponseEntity<AppResponse<String>> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = categoryService.createCategory(categoryCreateDTO, lang);
        log.info("Creating a region with the name: {}", categoryCreateDTO.getNameUz());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/detail/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a category", description = "Method used to update an existing category")
    public ResponseEntity<AppResponse<String>> updateCategory(@PathVariable String categoryId,
                                                              @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = categoryService.updateCategory(categoryId, categoryUpdateDTO, lang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a region", description = "Method used to delete an existing region")
    public ResponseEntity<AppResponse<String>> deleteCategory(@PathVariable(name = "categoryId") String categoryId,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = categoryService.deleteCategory(categoryId, lang);
        log.info("Deleting a region with the id: {}", categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all categories", description = "Method used to receive all existing categories")
    public ResponseEntity<List<CategoryInfoDTO>> getCategories(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<CategoryInfoDTO> response = categoryService.getCategories(lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lang")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all categories", description = "Method used to receive all existing categories")
    public ResponseEntity<List<CategoryShortInfoMapper>> getCategoriesByLang(@RequestParam(name = "lang") AppLanguage language) {

        List<CategoryShortInfoMapper> response = categoryService.getCategoriesByLang(language);
        return ResponseEntity.ok(response);
    }
}
