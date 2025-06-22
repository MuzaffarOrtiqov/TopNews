package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.comment.CommentReactionCreateDto;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.ArticleReactionService;
import api.kun.uz.service.CommentReactionService;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/comment/reaction")
@RestController
@Tag(name = "CommentReactionController", description = "A set of APIs to work with comment reaction")
@Slf4j
public class CommentReactionController {
    @Autowired
    private CommentReactionService commentReactionService;

    @PostMapping("")
    @Operation(summary = "Leave/Remove a reaction", description = "Method used to leave/Remove a reaction")
    public ResponseEntity<AppResponse<String>> leaveReaction(@Valid @RequestBody CommentReactionCreateDto dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = commentReactionService.leaveReaction(dto, lang);
        log.info("Leaving a reaction on comment with id {} :", dto.getCommentId());
        return ResponseEntity.ok(response);

    }
}
