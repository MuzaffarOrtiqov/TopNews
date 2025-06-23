package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.article.*;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.ArticleService;
import api.kun.uz.util.PageUtil;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/article/")
@Tag(name = "ArticleController", description = "A set of APIs to work with article")
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("/create")
    @Operation(summary = "Add new article", description = "Method used to create new article")
    public ResponseEntity<ArticleInfoDTO> createArticle(@Valid @RequestBody ArticleCreateUpdateDTO articleCreateUpdateDTO,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        ArticleInfoDTO response = articleService.createArticle(articleCreateUpdateDTO, lang);
        log.info("Creating an article with the name: {}", articleCreateUpdateDTO.getTitle());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{articleId}")
    @Operation(summary = "Update an article", description = "Method used to update an article")
    public ResponseEntity<ArticleInfoDTO> updateArticle(@PathVariable(name = "articleId") String articleId,
                                                        @Valid @RequestBody ArticleCreateUpdateDTO articleCreateUpdateDTO,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        ArticleInfoDTO response = articleService.updateArticle(articleId, articleCreateUpdateDTO, lang);
        log.info("Updating an article with the id: {}", articleId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{articleId}")
    @Operation(summary = "Delete an article", description = "Method used to delete an article")
    public ResponseEntity<AppResponse<String>> deleteArticle(@PathVariable(name = "articleId") String articleId,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = articleService.deleteArticle(articleId, lang);
        log.info("Deleting an article with the id: {}", articleId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/status/{articleId}")
    @Operation(summary = "Update an article status", description = "Method used to update an article status")
    public ResponseEntity<AppResponse<String>> updateArticleStatus(@PathVariable(name = "articleId") String articleId,
                                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = articleService.updateArticleStatus(articleId, lang);
        log.info("Updating an article status with the id: {}", articleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/{sectionId}")
    @Operation(summary = "Get articles with the same sectionId", description = "Method used to get articles with the same sectionId")
    public ResponseEntity<Page<ArticleShortInfo>> getArticlesBySectionId(@PathVariable(name = "sectionId") String sectionId,
                                                                         @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                         @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.getArticlesBySectionId(sectionId, PageUtil.giveProperPageNumbering(page), size, lang);
        log.info("Getting articles with section id: {}", sectionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recent")
    @Operation(summary = "Get latest articles except givenIdList", description = "Method used to get latest articles except givenArticleIdList")
    public ResponseEntity<Page<ArticleShortInfo>> getArticlesExceptGivenArticleIdList(@Valid @RequestBody ExceptArticleListDTO exceptArticleListDTO,
                                                                                      @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                                      @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.getArticlesExceptGivenArticleIdList(exceptArticleListDTO, PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get articles with the same categoryId", description = "Method used to get articles with the same categoryId")
    public ResponseEntity<Page<ArticleShortInfo>> getArticlesByCategoryId(@PathVariable(name = "categoryId") String categoryId,
                                                                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                          @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                          @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.getArticlesByCategoryId(categoryId, PageUtil.giveProperPageNumbering(page), size, lang);
        log.info("Getting articles with category id: {}", categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "Get articles with the same regionId", description = "Method used to get articles with the same regionId")
    public ResponseEntity<Page<ArticleFullInfoDTO>> getArticlesByRegionId(@PathVariable(name = "regionId") String regionId,
                                                                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                          @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                          @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleFullInfoDTO> response = articleService.getArticlesByRegionId(regionId, PageUtil.giveProperPageNumbering(page), size, lang);
        log.info("Getting articles with region id: {}", regionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "Get articles with  articleId", description = "Method used to get full info of an article")
    public ResponseEntity<ArticleFullInfoDTO> getArticleFullInfo(@PathVariable(name = "articleId") String articleId,
                                                                 @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        ArticleFullInfoDTO response = articleService.getArticleFullInfo(articleId, lang);
        log.info("Getting article full info with  id: {}", articleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tag")
    @Operation(summary = "Get articles by tag name", description = "Method used to get article by tag name")
    public ResponseEntity<List<ArticleShortInfo>> getArticlesByTagName(@RequestParam(name = "tagName") String tagName,
                                                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<ArticleShortInfo> response = articleService.getArticlesByTagName(tagName, lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest/{articleId}")
    @Operation(summary = "Get latest 4 articles on the same section", description = "Method used to get latest 4 articles on the same section")
    public ResponseEntity<List<ArticleShortInfo>> getLatest4ArticleOnSameSectionExcludingArticleId(@PathVariable(name = "articleId") String articleId,
                                                                                                   @RequestBody ArticleRequestSectionDTO dto,
                                                                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<ArticleShortInfo> response = articleService.getLatest4ArticleOnSameSectionExcludingArticleId(articleId, dto, lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/most-read/{articleId}")
    @Operation(summary = "Get most read 4 articles excluding current article", description = "Method used to get most read 4 articles excluding current article")
    public ResponseEntity<List<ArticleShortInfo>> getMostReadFourArticlesExcludingCurrentArticle(@PathVariable(name = "articleId") String articleId,
                                                                                                 @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<ArticleShortInfo> response = articleService.getMostReadFourArticlesExcludingCurrentArticle(articleId, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/view-count/{articleId}")
    @Operation(summary = "Increase article view count", description = "Method used to increase article view count")
    public ResponseEntity<AppResponse<String>> incrementViewCount(@PathVariable(name = "articleId") String articleId,
                                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = articleService.incrementViewCount(articleId, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/share-count/{articleId}")
    @Operation(summary = "Increase article share count", description = "Method used to increase article share count")
    public ResponseEntity<AppResponse<String>> incrementShareCount(@PathVariable(name = "articleId") String articleId,
                                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = articleService.incrementShareCount(articleId, lang);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Filter Articles", description = "Method used to filter articles by users")
    public ResponseEntity<Page<ArticleShortInfo>> filterArticlesForUsers(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                         @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                         @RequestBody ArticleFilterUserDTO articleFilterUserDTO,
                                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.filterArticlesForUsers(PageUtil.giveProperPageNumbering(page), size, articleFilterUserDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/private-moderator/filter")
    @Operation(summary = "Filter Articles By Moderators", description = "Method used to filter articles by moderators")
    public ResponseEntity<Page<ArticleShortInfo>> filterArticlesForModerators(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                              @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                              @RequestBody ArticleFilterModeratorDTO articleFilterModeratorDTO,
                                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.filterArticlesForModerators(PageUtil.giveProperPageNumbering(page), size, articleFilterModeratorDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/private-publisher/filter")
    @Operation(summary = "Filter Articles By Moderators", description = "Method used to filter articles by moderators")
    public ResponseEntity<Page<ArticleShortInfo>> filterArticlesForPublishers(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                              @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                              @RequestBody ArticleFilterPublisherDTO articleFilterPublisherDTO,
                                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ArticleShortInfo> response = articleService.filterArticlesForPublishers(PageUtil.giveProperPageNumbering(page), size, articleFilterPublisherDTO, lang);
        return ResponseEntity.ok(response);
    }

}
