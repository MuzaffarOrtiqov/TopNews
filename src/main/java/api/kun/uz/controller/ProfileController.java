package api.kun.uz.controller;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.ProfileFilterDTO;
import api.kun.uz.dto.profile.*;
import api.kun.uz.dto.sms.CodeConfirmDTO;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.service.ProfileService;
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

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "ProfileController", description = "A set of APIs to work with profile")
@Slf4j
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    @Operation(summary = "Update profile detail", description = "Method used to update details of a profile")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO profile,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateDetail(profile, lang);
        log.info("Update profile detail name: {}", profile.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    @Operation(summary = "Update profile password", description = "Method used to update password of a profile")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfileUpdatePasswordDTO profile,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updatePassword(profile, lang);
        log.info("Password is being updated");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username")
    @Operation(summary = "Update profile username", description = "Method used to update username of a profile")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUpdateUsernameDTO profileDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsername(profileDTO, lang);
        log.info("Old username is being updated to username: {}", profileDTO.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username-confirmation")
    @Operation(summary = "Confirm profile's username", description = "Method used to confirm username of a profile")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsernameConfirm(codeConfirmDTO, lang);
        log.info("Username confirmation");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/create")
    @Operation(summary = "Create a new profile", description = "Method used to create a new profile")
    public ResponseEntity<ProfileInfoDTO> createProfile(@Valid @RequestBody ProfileCreateDTO profileCreateDTO,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        ProfileInfoDTO response = profileService.createProfile(profileCreateDTO, lang);
        log.info("Creating a profile with the name: {}", profileCreateDTO.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/detail/{profileId}")
    @Operation(summary = "Update profile detail by admin", description = "Method used to update details of a profile by admin")
    public ResponseEntity<ProfileInfoDTO> updateDetail(@PathVariable(name = "profileId") String profileId,
                                                       @Valid @RequestBody ProfileDetailUpdateAdminDTO profile,
                                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        ProfileInfoDTO response = profileService.updateDetailByAdmin(profileId, profile, lang);
        log.info("Update profile detail with id : {}", profileId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/pagination")
    @Operation(summary = "Pagination", description = "Method used to get all profiles in paginated form")
    public ResponseEntity<Page<ProfileInfoDTO>> pagination(@RequestParam(name = "page") Integer page,
                                                           @RequestParam(name = "size") Integer size,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ProfileInfoDTO> response = profileService.pagination(PageUtil.giveProperPageNumbering(page), size, lang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/delete/{profileId}")
    @Operation(summary = "Delete an existing profile", description = "Method used to delete an existing profile")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable(name = "profileId") String profileId,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.deleteProfile(profileId, lang);
        log.info("Deleting profile with id : {}", profileId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/filter")
    @Operation(summary = "Filter users", description = "Method used to fileter users")
    public ResponseEntity<Page<ProfileInfoDTO>> filter(@RequestParam(name = "page",defaultValue = "1") Integer page,
                                                       @RequestParam(name = "size",defaultValue = "5") Integer size,
                                                       @RequestBody ProfileFilterDTO profileFilterDTO,
                                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        Page<ProfileInfoDTO> response = profileService.filter(PageUtil.giveProperPageNumbering(page), size, profileFilterDTO,lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/photo")
    @Operation(summary = "Update profile photo", description = "Method used to update photo of a profile")
    public ResponseEntity<AppResponse<String>> updateProfilePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO profileUpdateDTO,
                                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updateProfilePhoto(profileUpdateDTO, lang);
        log.info("Profile photo updated");
        return ResponseEntity.ok(response);
    }

}
