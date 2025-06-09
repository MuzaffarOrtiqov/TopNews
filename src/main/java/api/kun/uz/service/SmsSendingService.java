package api.kun.uz.service;

import api.kun.uz.dto.sms.SmsAuthDTO;
import api.kun.uz.dto.sms.SmsRequestDTO;
import api.kun.uz.dto.sms.SmsSendResponseDTO;
import api.kun.uz.entity.SmsProviderTokenHolderEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.SmsType;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.SmsProviderTokenHolderRepository;
import api.kun.uz.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsSendingService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${eskiz.api}")
    private String smsUrl;
    @Value("${eskiz.login}")
    private String accountLogin;
    @Value("${eskiz.password}")
    private String accountPassword;

    @Autowired
    private SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    private final Long smsLimit = 3l;

    public void sendPasswordResetSms(String username, AppLanguage lang) {
        String message = resourceBundleMessageService.getMessage("sms.reset.password.code", lang);
        String code = RandomUtil.getRandomSmsCode();
        sendSms(username, message, code, SmsType.RESET_PASSWORD);
    }
    public void sendRegistrationSms(String phone, AppLanguage language) {
        String message = resourceBundleMessageService.getMessage("phone.confirm.sent", language);
        String code = RandomUtil.getRandomSmsCode();
        sendSms(phone, message, code, SmsType.REGISTRATION);
    }
    private SmsSendResponseDTO sendSms(String phoneNumber, String message, String code, SmsType smsType) {
        Long count = smsHistoryService.getSmsCount(phoneNumber);
        if (count > smsLimit) {
            log.warn("Sms limit reached phone number: {}", phoneNumber);
            throw new AppBadException("Try again later");
        }
        SmsSendResponseDTO result = sendSms(phoneNumber, message);
        smsHistoryService.createSmsHistory(phoneNumber, message, code, smsType);
        return result;
    }
    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {
        //header
        String token = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);
        //body
        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMobile_phone(phoneNumber);
        smsRequestDTO.setMessage(message);
        smsRequestDTO.setFrom("4546");
        //send request
        try {
            HttpEntity<SmsRequestDTO> entity = new HttpEntity<>(smsRequestDTO, headers);
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(smsUrl + "/message/sms/send", HttpMethod.POST, entity, SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Send Sms failed phone :{}, message: {}, error: {}",phoneNumber, message,e.getMessage());
            throw new RuntimeException(e);
        }

    }
    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findFirstByOrderByIdDesc();
        if (optional.isEmpty()) {
            String token = getTokenFromProvider();
            SmsProviderTokenHolderEntity smsProviderTokenHolderEntity = new SmsProviderTokenHolderEntity();
            smsProviderTokenHolderEntity.setToken(token);
            smsProviderTokenHolderEntity.setCreatedDate(LocalDateTime.now());
            smsProviderTokenHolderEntity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(smsProviderTokenHolderEntity);
            return token;
        }
        SmsProviderTokenHolderEntity entity = optional.get();
        if (!entity.getCreatedDate().isBefore(entity.getExpiredDate())) {
            String token = getTokenFromProvider();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            return token;
        }
        return entity.getToken();

    }
    private String getTokenFromProvider() {
        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);

        String response = restTemplate.postForObject(smsUrl + "/auth/login", smsAuthDTO, String.class);
        try {
            JsonNode parent = new ObjectMapper().readTree(response);
            JsonNode child = parent.get("data");
            String token = child.get("token").asText();
            return token;
        } catch (JsonProcessingException e) {
            log.error("Get token :{}, error: {}",accountLogin, e.getMessage());
            throw new RuntimeException(e);
        }

    }


}
