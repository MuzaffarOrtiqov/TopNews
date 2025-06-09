package api.kun.uz.service;

import api.kun.uz.dto.AppResponse;
import api.kun.uz.dto.auth.AuthDTO;
import api.kun.uz.dto.auth.RegistrationDTO;
import api.kun.uz.dto.auth.ResetPasswordConfirmDTO;
import api.kun.uz.dto.auth.ResetPasswordDTO;
import api.kun.uz.dto.profile.ProfileResponseDTO;
import api.kun.uz.dto.sms.SmsResendDTO;
import api.kun.uz.dto.sms.SmsVerificationDTO;
import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.GeneralStatus;
import api.kun.uz.enums.ProfileRole;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ProfileRepository;
import api.kun.uz.repository.ProfileRoleRepository;
import api.kun.uz.util.JwtUtil;
import api.kun.uz.util.ValidityUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SmsSendingService smsSendingService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;


    public AppResponse<String> registration(RegistrationDTO dto, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
            } else {
                log.warn("Profile {} already exists", profile.getId());
                throw new AppBadException(resourceBundleMessageService.getMessage("email.phone.exists", lang));
            }
        }

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(dto.getName());
        profileEntity.setUsername(dto.getUsername());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        profileEntity.setStatus(GeneralStatus.IN_REGISTRATION);
        profileRepository.save(profileEntity);
        profileRoleService.create(profileEntity.getId(), ProfileRole.ROLE_USER);
        // check if email valid
        if (ValidityUtil.isValidEmail(dto.getUsername())) {
            emailSendingService.sendRegistrationEmail(dto.getUsername(), profileEntity.getId(), lang.name());
            return new AppResponse<>(resourceBundleMessageService.getMessage("email.confirm.sent", lang, dto.getUsername()));
        }
        //check if phone valid
        if (ValidityUtil.isValidPhone(dto.getUsername())) {
            smsSendingService.sendRegistrationSms(dto.getUsername(), lang);
            return new AppResponse<>(resourceBundleMessageService.getMessage("phone.confirm.sent", lang, dto.getUsername()));
        }
        throw new AppBadException(resourceBundleMessageService.getMessage("email.phone.invalid", lang));
    }

    public ProfileResponseDTO regVerificationEmail(String token, AppLanguage lang) {
        try {
            String profileId = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profileEntity = profileService.findProfileById(profileId, lang);
            if (profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRepository.updateStatus(profileId, GeneralStatus.ACTIVE);
                return getLoginResponse(profileEntity);
            }
        } catch (JwtException e) {
            throw new AppBadException(resourceBundleMessageService.getMessage("token.invalid.expired", lang));
        }
        log.info("Email verification failed {}", token);
        throw new AppBadException(resourceBundleMessageService.getMessage("verification.failed", lang));
    }

    private ProfileResponseDTO getLoginResponse(ProfileEntity profileEntity) {
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.setName(profileEntity.getName());
        profileResponseDTO.setUsername(profileEntity.getUsername());
        profileResponseDTO.setRoleList(profileRoleRepository.getRoles(profileEntity.getId()));
        //profileResponseDTO.setAttachDTO(attachService.attachDTO(profileEntity.getPhotoId()));

        profileResponseDTO.setJwt(JwtUtil.encode(profileEntity.getId(), profileEntity.getUsername(), profileResponseDTO.getRoleList()));
        return profileResponseDTO;
    }

    public ProfileResponseDTO regVerificationSms(SmsVerificationDTO dto, AppLanguage lang) {
        //check if exists
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            log.info("Profile not found {}", dto.getPhone());
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }
        // check for  status
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            log.info("Verification failed: {}", dto.getPhone());
            throw new AppBadException(resourceBundleMessageService.getMessage("verification.failed", lang));
        }

        // check if code matches
        smsHistoryService.check(dto.getPhone(), dto.getCode(), lang);
        //update
        profileRepository.updateStatus(profileEntity.getId(), GeneralStatus.ACTIVE);

        return getLoginResponse(profileEntity);
    }

    public ProfileResponseDTO login(AuthDTO authDTO, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername());
        if (optional.isEmpty()) {
            log.info("Login failed username: {}", authDTO.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("wrong.password.username", lang));
        }
        ProfileEntity profileEntity = optional.get();
        if (!bCryptPasswordEncoder.matches(authDTO.getPassword(), profileEntity.getPassword())) {
            log.info("Login failed username: {}", authDTO.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("wrong.password.username", lang));
        }
        if (!profileEntity.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Wrong status username {}", authDTO.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("wrong.status", lang));
        }
        return getLoginResponse(profileEntity);
    }

    public AppResponse<String> resetPassword(ResetPasswordDTO dto, AppLanguage lang) {
        //check if exits
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            log.info("Profile not found username {}", dto.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }
        // check for  status
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.info("Wrong status {}", dto.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("wrong.status", lang));
        }
        //check if email valid
        if (ValidityUtil.isValidEmail(dto.getUsername())) {
            emailSendingService.sendPasswordResetEmail(dto.getUsername(), lang);

        }
        //check if phone valid
        if (ValidityUtil.isValidPhone(dto.getUsername())) {
            smsSendingService.sendPasswordResetSms(dto.getUsername(), lang);

        }
        return new AppResponse<>(resourceBundleMessageService.getMessage("reset.password.username.sent", lang));
    }

    public AppResponse<String> resetPasswordConfirm(ResetPasswordConfirmDTO dto, AppLanguage lang) {
        //check if exits
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            log.info("Profile not found username {}", dto.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.info("Wrong status {}", dto.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("wrong.status", lang));
        }
        // check if email valid
        if (ValidityUtil.isValidEmail(dto.getUsername())) {
            emailHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);
        }
        //check if phone valid
        if (ValidityUtil.isValidPhone(dto.getUsername())) {
            smsHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);
        }
        //update password
        profileRepository.updatePassword(profileEntity.getId(), bCryptPasswordEncoder.encode(dto.getPassword()));
        //response
        return new AppResponse<>(resourceBundleMessageService.getMessage("reset.password.success", lang));
    }

    public AppResponse<String> regVerificationSmsResend(SmsResendDTO dto, AppLanguage lang) {
        //check if exits
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            log.info("Profile not found username {}", dto.getPhone());
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }
        // check for  status
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            log.info("Verification failed", dto.getPhone());
            throw new AppBadException(resourceBundleMessageService.getMessage("verification.failed", lang));
        }
        //check if phone valid
        smsSendingService.sendRegistrationSms(dto.getPhone(), lang);
        return new AppResponse<>(resourceBundleMessageService.getMessage("sms.resend", lang));

    }
}
