package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.FilterResultDTO;
import api.kun.uz.dto.ProfileFilterDTO;
import api.kun.uz.dto.profile.*;
import api.kun.uz.dto.sms.CodeConfirmDTO;
import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.CustomFilterRepository;
import api.kun.uz.repository.ProfileRepository;
import api.kun.uz.repository.ProfileRoleRepository;
import api.kun.uz.util.JwtUtil;
import api.kun.uz.util.SpringSecurityUtil;
import api.kun.uz.util.ValidityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SmsSendingService smsSendingService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private CustomFilterRepository customFilterRepository;
    @Autowired
    private AttachService attachService;


    public ProfileEntity findProfileById(String profileId, AppLanguage lang) {
        Optional<ProfileEntity> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            return profile.get();
        }
        throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
    }

    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO profile, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentProfileId();
        profileRepository.updateProfileName(profile.getName(), profile.getSurname(), userId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.name.updated", lang));
    }

    public AppResponse<String> updatePassword(ProfileUpdatePasswordDTO profile, AppLanguage lang) {
        String profilePassword = SpringSecurityUtil.getCurrentProfilePassword();
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        if (!bCryptPasswordEncoder.matches(profile.getCurrentPassword(), profilePassword)) {
            log.warn("Password mismatch: userId:{}", profileId);
            throw new AppBadException(resourceBundleMessageService.getMessage("password.not.match", lang));
        }
        profileRepository.updatePassword(bCryptPasswordEncoder.encode(profile.getNewPassword()), profileId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("password.update.success", lang));
    }

    public AppResponse<String> updateUsername(ProfileUpdateUsernameDTO profileDTO, AppLanguage lang) {
        //check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(profileDTO.getUsername());
        if (optional.isPresent()) {
            log.info("Username already in use: {}", profileDTO.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("email.phone.exists", lang));
        }

        //send confirm code
        if (ValidityUtil.isValidEmail(profileDTO.getUsername())) {
            emailSendingService.sendUsernameChangeConfirmEmail(profileDTO.getUsername(), lang);
        }
        if (ValidityUtil.isValidPhone(profileDTO.getUsername())) {
            smsSendingService.sendUsernameChangeConfirmSms(profileDTO.getUsername(), lang);
        }
        if (!ValidityUtil.isValidEmail(profileDTO.getUsername()) && !ValidityUtil.isValidPhone(profileDTO.getUsername())) {
            throw new AppBadException(resourceBundleMessageService.getMessage("email.phone.invalid", lang));
        }

        String userId = SpringSecurityUtil.getCurrentProfileId();
        profileRepository.updateTempUsername(profileDTO.getUsername(), userId);

        return new AppResponse<>(resourceBundleMessageService.getMessage("confirm.code.sent", lang, profileDTO.getUsername()));
    }

    public AppResponse<String> updateUsernameConfirm(CodeConfirmDTO codeConfirmDTO, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentProfileId();
        ProfileEntity profileEntity = findProfileById(userId, lang);
        String tempUsername = profileEntity.getTempUsername();
        // check if email valid
        if (ValidityUtil.isValidEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, codeConfirmDTO.getCode(), lang);
        }
        //check if phone valid
        if (ValidityUtil.isValidPhone(tempUsername)) {
            smsHistoryService.check(tempUsername, codeConfirmDTO.getCode(), lang);
        }
        //update username after checking
        profileRepository.updateUsername(userId, tempUsername);

        List<ProfileRole> roleList = profileRoleRepository.getRoles(profileEntity.getId());
        String jwt = JwtUtil.encode(userId, tempUsername, roleList);
        return new AppResponse<>(jwt, resourceBundleMessageService.getMessage("username.update.success", lang));
    }


    public ProfileInfoDTO createProfile(ProfileCreateDTO profileCreateDTO, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(profileCreateDTO.getUsername());
        if (optional.isPresent()) {
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.exists", lang));
        }
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(profileCreateDTO.getName());
        profileEntity.setSurname(profileCreateDTO.getSurname());
        profileEntity.setUsername(profileCreateDTO.getUsername());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(profileCreateDTO.getPassword()));
        profileEntity.setStatus(profileCreateDTO.getStatus());
        profileRepository.save(profileEntity);
        profileRoleService.create(profileEntity.getId(), profileCreateDTO.getRoleList());
        return toDTO(profileEntity);
    }

    public ProfileInfoDTO updateDetailByAdmin(String profileId, ProfileDetailUpdateAdminDTO profile, AppLanguage lang) {
        ProfileEntity profileEntity = findProfileById(profileId, lang);
        profileEntity.setName(profile.getName());
        profileEntity.setSurname(profile.getSurname());
        profileEntity.setStatus(profile.getStatus());
        profileRepository.save(profileEntity);
        profileRoleService.updateRoles(profileId, profile.getRoles());
        return toDTO(profileEntity);
    }

    public Page<ProfileInfoDTO> pagination(Integer page, Integer size, AppLanguage lang) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileEntity> profileEntityPage = profileRepository.findAllProfiles(pageable);
        long totalElements = profileEntityPage.getTotalElements();
        List<ProfileInfoDTO> profileInfoDTOList = profileEntityPage.stream().map(this::toDTO).toList();
        return new PageImpl<>(profileInfoDTOList, pageable, totalElements);

    }

    public AppResponse<String> deleteProfile(String profileId, AppLanguage lang) {
        findProfileById(profileId, lang);
        profileRepository.deleteProfileById(profileId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.delete.success", lang));
    }

    public Page<ProfileInfoDTO> filter(Integer page, Integer size, ProfileFilterDTO profileFilterDTO, AppLanguage lang) {
        FilterResultDTO<Object[]> result = customFilterRepository.filter(profileFilterDTO, page, size);
        List<ProfileInfoDTO> profileInfoDTOList = result
                .getList()
                .stream()
                .map(this::toDTO).toList();
        return new PageImpl<>(profileInfoDTOList, PageRequest.of(page, size), result.getCount());
    }

    //util
    private ProfileInfoDTO toDTO(ProfileEntity profileEntity) {
        ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO();
        profileInfoDTO.setName(profileEntity.getName());
        profileInfoDTO.setSurname(profileEntity.getSurname());
        profileInfoDTO.setUsername(profileEntity.getUsername());
        profileInfoDTO.setStatus(profileEntity.getStatus());
        profileInfoDTO.setRoles(profileRoleRepository.getRoles(profileEntity.getId()));
        return profileInfoDTO;
    }

    private ProfileInfoDTO toDTO(Object[] obj) {
        ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO();
        profileInfoDTO.setName((String) obj[0]);
        profileInfoDTO.setSurname((String) obj[1]);
        profileInfoDTO.setUsername((String) obj[2]);
        profileInfoDTO.setStatus((GeneralStatus) obj[3]);
        profileInfoDTO.setCreatedDate((LocalDateTime) obj[4]);
        String roles = String.valueOf(obj[5]);
        List<ProfileRole> profileRoles = Arrays.stream(roles.split(",")).map(ProfileRole::valueOf).toList();
        profileInfoDTO.setRoles(profileRoles);
        return profileInfoDTO;
    }


    public AppResponse<String> updateProfilePhoto(ProfilePhotoUpdateDTO profileUpdateDTO, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentProfileId();
        ProfileEntity profileEntity = findProfileById(userId, lang);

        String currentPhotoId = profileEntity.getPhotoId();
        String newPhotoId = profileUpdateDTO.getPhotoId();

        if (!Objects.equals(newPhotoId, currentPhotoId)) {
            if (currentPhotoId != null) {
                attachService.delete(currentPhotoId, lang);
            }
            profileRepository.updateProfilePhoto(userId, newPhotoId);
        }
        profileRepository.updateProfilePhoto(userId, profileUpdateDTO.getPhotoId());
        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.photo.updated", lang));
    }
}