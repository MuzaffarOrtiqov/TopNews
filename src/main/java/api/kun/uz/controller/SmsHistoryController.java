package api.kun.uz.controller;

import api.kun.uz.dto.email_history.DateRequestDto;
import api.kun.uz.dto.sms_history.PhoneRequestDto;
import api.kun.uz.dto.sms_history.SmsHistoryResponseDto;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.SmsHistoryService;
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
@RequestMapping("/api/v1/sms-history")
@Tag(name = "EmailHistoryController", description = "A set of APIs to work with SmsHistory")
@Slf4j
public class SmsHistoryController {
    @Autowired
    private SmsHistoryService smsHistoryService;

    @GetMapping("/")
    @Operation(summary = "Get sms history", description = "Method used to get history by sms")
    public ResponseEntity<List<SmsHistoryResponseDto>> getSmsHistoryByPhone(@Valid @RequestBody PhoneRequestDto phoneRequestDto,
                                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<SmsHistoryResponseDto> response = smsHistoryService.getSmsHistoryByPhone(phoneRequestDto, lang);
        log.info("Getting a sms history of user with the phone: {}", phoneRequestDto.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date")
    @Operation(summary = "Get sms history", description = "Method used to get history by date")
    public ResponseEntity<List<SmsHistoryResponseDto>> getSmsHistoryByDate(@Valid @RequestBody DateRequestDto dateRequestDto,
                                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        List<SmsHistoryResponseDto> response = smsHistoryService.getSmsHistoryByDate(dateRequestDto, lang);
        log.info("Getting a sms history by given date: {}", dateRequestDto.getDate());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @Operation(summary = "Get sms history pagination", description = "Method used to get sms history pagination")
    public ResponseEntity<Page<SmsHistoryResponseDto>> getAllSmsHistoriesPaged(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                               @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                               @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<SmsHistoryResponseDto> response = smsHistoryService.getAllSmsHistoriesPaged(PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }
}
