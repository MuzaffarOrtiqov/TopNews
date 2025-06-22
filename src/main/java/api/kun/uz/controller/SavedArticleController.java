package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.saved_article.SavedArticleCreateDto;
import api.kun.uz.dto.saved_article.SavedArticleResponseDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.SavedArticleService;
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
@RequestMapping("/api/v1/saved-article/")
@Tag(name = "SavedArticleController", description = "A set of APIs to work with saved article")
@Slf4j
public class SavedArticleController {
    @Autowired
    private SavedArticleService savedArticleService;

    @PostMapping("/create")
    @Operation(summary = "Save a new article", description = "Method used to save a new article")
    public ResponseEntity<AppResponse<String>> createSavedArticle(@Valid @RequestBody SavedArticleCreateDto savedArticleCreateDto,
                                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = savedArticleService.createSavedArticle(savedArticleCreateDto, lang);
        log.info("Saving an article with the id: {}", savedArticleCreateDto.getArticleId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{saved-articleId}")
    @Operation(summary = "Delete a saved article", description = "Method used to delete a saved article")
    public ResponseEntity<AppResponse<String>> deleteSavedArticle(@PathVariable(name = "saved-articleId") String savedArticleId,
                                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = savedArticleService.deleteSavedArticle(savedArticleId, lang);
        log.info("Deleting a saved article with the id: {}", savedArticleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    @Operation(summary = "Get All Saved Article", description = "Method used to get all saved articles of a user")
    public ResponseEntity<List<SavedArticleResponseDTO>> getProfileSavedArticles(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<SavedArticleResponseDTO> response = savedArticleService.getProfileSavedArticles(lang);
        return ResponseEntity.ok(response);
    }
}
