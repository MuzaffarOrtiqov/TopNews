package api.kun.uz.controller;

import api.kun.uz.dto.email_history.EmailRequestDto;
import api.kun.uz.dto.email_history.DateRequestDto;
import api.kun.uz.dto.email_history.EmailHistoryResponseDto;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.EmailHistoryService;
import api.kun.uz.util.PageUtil;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/email-history")
@Tag(name = "EmailHistoryController", description = "A set of APIs to work with EmailHistory")
@Slf4j
public class EmailHistoryController {
    @Autowired
    private EmailHistoryService emailHistoryService;

    @GetMapping("/")
    @Operation(summary = "Get email history", description = "Method used to get history by email")
    public ResponseEntity<List<EmailHistoryResponseDto>> getEmailHistory(@Valid @RequestBody EmailRequestDto emailRequestDto,
                                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<EmailHistoryResponseDto> response = emailHistoryService.getEmailHistory(emailRequestDto, lang);
        log.info("Getting a email history of user with the email: {}", emailRequestDto.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date")
    @Operation(summary = "Get email history", description = "Method used to get history by email")
    public ResponseEntity<List<EmailHistoryResponseDto>> getEmailHistoryByDate(@Valid @RequestBody DateRequestDto dateRequestDto,
                                                                               @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<EmailHistoryResponseDto> response = emailHistoryService.getEmailHistoryByDate(dateRequestDto, lang);
        log.info("Getting a email history by given date: {}", dateRequestDto.getDate());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @Operation(summary = "Get email history pagination", description = "Method used to get email history pagination")
    public ResponseEntity<Page<EmailHistoryResponseDto>> getAllEmailHistoriesPaged(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                                   @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<EmailHistoryResponseDto> response = emailHistoryService.getAllEmailHistoriesPaged(PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }
}
