package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.region.RegionCreateDTO;
import api.kun.uz.dto.region.RegionInfoDTO;
import api.kun.uz.dto.region.RegionUpdateDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.mapper.RegionShortInfoMapper;
import api.kun.uz.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region/")
@Tag(name = "RegionController", description = "A set of APIs to work with region")
@Slf4j
public class RegionController {
    @Autowired
    private RegionService regionService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add new region", description = "Method used to create new region")
    public ResponseEntity<AppResponse<String>> createRegion(@Valid @RequestBody RegionCreateDTO regionCreateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = regionService.createRegion(regionCreateDTO, lang);
        log.info("Creating a region with the name: {}", regionCreateDTO.getNameUz());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/detail/{regionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a region", description = "Method used to update an existing region")
    public ResponseEntity<AppResponse<String>> updateRegion(@PathVariable String regionId,
                                                            @Valid @RequestBody RegionUpdateDTO regionUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = regionService.updateRegion(regionId, regionUpdateDTO, lang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{regionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a region", description = "Method used to delete an existing region")
    public ResponseEntity<AppResponse<String>> deleteRegion(@PathVariable(name = "regionId") String regionId,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = regionService.deleteRegion(regionId, lang);
        log.info("Deleting a region with the id: {}", regionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all regions", description = "Method used to receive all existing regions")
    public ResponseEntity<List<RegionInfoDTO>> getRegions(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<RegionInfoDTO> response = regionService.getRegions(lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lang")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all regions", description = "Method used to receive all existing regions")
    public ResponseEntity<List<RegionShortInfoMapper>> getRegionsByLang(@RequestParam(name = "lang") AppLanguage language) {

        List<RegionShortInfoMapper> response = regionService.getRegionsByLang(language);
        return ResponseEntity.ok(response);
    }
}
