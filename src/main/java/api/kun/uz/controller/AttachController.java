package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.attach.AttachDTO;
import api.kun.uz.dto.profile.ProfileInfoDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.AttachService;
import api.kun.uz.util.PageUtil;
import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@Tag(name = "AttachController", description = "A set of APIs to work with attach")
@Slf4j
public class AttachController {
    @Autowired
    private AttachService attachService;


    @PostMapping("/upload")
    @Operation(summary = "Upload multipart file", description = "Method used to upload any sort of file")
    public ResponseEntity<AttachDTO> create(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }

    @GetMapping("/open/{fileId}")
    @Operation(summary = "Open multipart file", description = "Method used to open any type of file")
    public ResponseEntity<Resource> open(@PathVariable String fileId,
                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return attachService.open(fileId, lang);
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download multipart file", description = "Method used to download any type of file")
    public ResponseEntity<byte[]> download(@PathVariable String fileId,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return attachService.download(fileId, lang);
    }

    @PutMapping("/pagination")
    @Operation(summary = "Pagination", description = "Method used to get all attaches in paginated form")
    public ResponseEntity<Page<AttachDTO>> pagination(@RequestParam(name = "page") Integer page,
                                                      @RequestParam(name = "size") Integer size,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<AttachDTO> response = attachService.pagination(PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{fileId}")
    @Operation(summary = "Delete Attach", description = "Method used to get delete a particular attach")
    public ResponseEntity<AppResponse<String>> deleteAttach(@PathVariable(name = "fileId") String fileId,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = attachService.deleteAttach(fileId, lang);
        return ResponseEntity.ok(response);
    }
}
