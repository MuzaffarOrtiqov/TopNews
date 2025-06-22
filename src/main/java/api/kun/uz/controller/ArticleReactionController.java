package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.article.ArticleReactionCreateDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.ArticleReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/article/reaction")
@RestController
@Tag(name = "ArticleReactionController", description = "A set of APIs to work with article reaction")
@Slf4j
public class ArticleReactionController {
    @Autowired
    private ArticleReactionService articleReactionService;

    @PostMapping("")
    @Operation(summary = "Leave/Remove a reaction", description = "Method used to leave/Remove a reaction")
    public ResponseEntity<AppResponse<String>> leaveReaction(@Valid @RequestBody ArticleReactionCreateDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = articleReactionService.leaveReaction(dto, lang);
        log.info("Leaving a reaction on article with id {} :", dto.getArticleId());
        return ResponseEntity.ok(response);

    }
}
