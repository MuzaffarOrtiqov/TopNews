package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.comment.CommentCreateDTO;
import api.kun.uz.dto.comment.CommentFilterDTO;
import api.kun.uz.dto.comment.CommentResponseDto;
import api.kun.uz.dto.comment.CommentUpdateDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.CommentService;
import api.kun.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/comment/")
@Tag(name = "CommentController", description = "A set of APIs to work with comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    @Operation(summary = "Add new comment", description = "Method used to create new comment")
    public ResponseEntity<AppResponse<String>> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = commentService.createComment(commentCreateDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{commentId}")
    @Operation(summary = "Update a comment", description = "Method used to update an existing comment")
    public ResponseEntity<AppResponse<String>> updateComment(@PathVariable(name = "commentId") String commentId,
                                                             @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = commentService.updateComment(commentId, commentUpdateDTO, lang);
        log.info("Updating a comment with id: " + commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "Delete a comment", description = "Method used to delete an existing comment")
    public ResponseEntity<AppResponse<String>> deleteComment(@PathVariable(name = "commentId") String commentId,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = commentService.deleteComment(commentId, lang);
        log.info("Deleting a comment with id: " + commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{commentId}")
    @Operation(summary = "Get comment full details ", description = "Method used to get comment full details")
    public ResponseEntity<CommentResponseDto> getCommentDetails(@PathVariable(name = "commentId") String commentId,
                                                                @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        CommentResponseDto response = commentService.getCommentDetails(commentId, lang);
        log.info("Getting comment details for comment with id: " + commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all comments ", description = "Method used to get all comments by admin")
    public ResponseEntity<Page<CommentResponseDto>> getComments(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<CommentResponseDto> response = commentService.getComments(PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Filter comments ", description = "Method used to filter comments by admin")
    public ResponseEntity<Page<CommentResponseDto>> filter(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                           @Valid @RequestBody CommentFilterDTO commentFilterDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<CommentResponseDto> response = commentService.filter(PageUtil.giveProperPageNumbering(page), size, commentFilterDTO, lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/replied-comment/{commentId}")
    @Operation(summary = "Get all replied comments ", description = "Method used to get all replied comments")
    public ResponseEntity<List<CommentResponseDto>> getAllCommentReplies(@PathVariable(name = "commentId") String commentId,
                                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<CommentResponseDto> response = commentService.getAllCommentReplies(commentId, lang);
        log.info("Getting all replied comments with id: " + commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Get comments of an article", description = "Method used to get comments of an article")
    public ResponseEntity<List<CommentResponseDto>> getArticleComments(@PathVariable(name = "articleId") String articleId,
                                                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<CommentResponseDto> response = commentService.getArticleComments(articleId, lang);
        return ResponseEntity.ok(response);
    }


}
