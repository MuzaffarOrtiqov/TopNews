package api.kun.uz.service;

import api.kun.uz.config.CustomUserDetails;
import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.auth.AuthRequestDTO;
import api.kun.uz.dto.auth.AuthResponseDTO;
import api.kun.uz.dto.profile.*;
import api.kun.uz.dto.region.RegionCreateDTO;
import api.kun.uz.dto.sms.CodeConfirmDTO;
import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ProfileRepository;
import api.kun.uz.repository.ProfileRoleRepository;
import api.kun.uz.util.JwtUtil;
import api.kun.uz.util.SpringSecurityUtil;
import api.kun.uz.util.ValidityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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


    public AppResponse<String> createProfile(ProfileCreateDTO profileCreateDTO, AppLanguage lang) {
        findProfileById(profileCreateDTO.getUsername(), lang);
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(profileCreateDTO.getName());
        profileEntity.setSurname(profileCreateDTO.getSurname());
        profileEntity.setUsername(profileCreateDTO.getUsername());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(profileCreateDTO.getPassword()));
        profileEntity.setStatus(profileCreateDTO.getStatus());
        profileRepository.save(profileEntity);
        profileRoleService.create(profileEntity.getId(), profileCreateDTO.getRoleList());
        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.create.success", lang));
    }
}