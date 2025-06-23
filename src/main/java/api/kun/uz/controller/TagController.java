package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.tag.TagCreateDto;
import api.kun.uz.dto.tag.TagResponseDto;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.TagService;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tag/")
@Tag(name = "TagController", description = "A set of APIs to work with tag")
@Slf4j
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping("/create")
    @Operation(summary = "Create a new tag", description = "Method used to create new tag")
    public ResponseEntity<AppResponse<String>> createTag(@Valid @RequestBody TagCreateDto tagCreateDto,
                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = tagService.createTag(tagCreateDto, lang);
        log.info("Creating a tag with the name: {}", tagCreateDto.getTagName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Get email history", description = "Method used to get history by email")
    public ResponseEntity<List<TagResponseDto>> getAllTags(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<TagResponseDto> response = tagService.getAllTags(lang);
        return ResponseEntity.ok(response);
    }
}
