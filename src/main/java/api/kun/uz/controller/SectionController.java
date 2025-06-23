package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.category.CategoryUpdateDTO;
import api.kun.uz.dto.section.SectionCreateDTO;
import api.kun.uz.dto.section.SectionInfoDTO;
import api.kun.uz.dto.section.SectionUpdateDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.mapper.SectionShortInfoMapper;
import api.kun.uz.service.SectionService;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/section/")
@Tag(name = "SectionController", description = "A set of APIs to work with section")
@Slf4j
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @PostMapping("/create")
    @Operation(summary = "Add new section", description = "Method used to create new section")
    public ResponseEntity<AppResponse<String>> createSection(@Valid @RequestBody SectionCreateDTO sectionCreateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = sectionService.createSection(sectionCreateDTO, lang);
        log.info("Creating a region with the name: {}", sectionCreateDTO.getNameUz());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/detail/{sectionId}")
    @Operation(summary = "Update a section", description = "Method used to update an existing section")
    public ResponseEntity<AppResponse<String>> updateSection(@PathVariable String sectionId,
                                                            @Valid @RequestBody SectionUpdateDTO sectionUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = sectionService.updateSection(sectionId, sectionUpdateDTO, lang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{sectionId}")
    @Operation(summary = "Delete a section", description = "Method used to delete an existing section")
    public ResponseEntity<AppResponse<String>> deleteSection(@PathVariable(name = "sectionId") String sectionId,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = sectionService.deleteSection(sectionId, lang);
        log.info("Deleting a section with the id: {}", sectionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all sections", description = "Method used to receive all existing sections")
    public ResponseEntity<List<SectionInfoDTO>> getSections(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<SectionInfoDTO> response = sectionService.getSections(lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lang")
    @Operation(summary = "Get all sections", description = "Method used to receive all existing sections")
    public ResponseEntity<List<SectionShortInfoMapper>> getSectionsByLang(@RequestParam(name = "lang") AppLanguage language) {

        List<SectionShortInfoMapper> response = sectionService.getSectionsByLang(language);
        return ResponseEntity.ok(response);
    }
}
