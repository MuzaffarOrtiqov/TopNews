package api.kun.uz.service;

import api.kun.uz.entity.SmsHistoryEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.SmsType;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.SmsHistoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsHistoryService {
    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public Long getSmsCount(String phoneNumber) {
        LocalDateTime now = LocalDateTime.now();
        Long count = smsHistoryRepository.countByPhoneAndCreatedDateBetween(phoneNumber, now.minusMinutes(1), now);
        return count;
    }

    public void createSmsHistory(String phoneNumber, String message, String code, SmsType smsType) {
        SmsHistoryEntity smsHistory = new SmsHistoryEntity();
        smsHistory.setSmsType(smsType);
        smsHistory.setMessage(message);
        smsHistory.setCode(code);
        smsHistory.setPhone(phoneNumber);
        smsHistoryRepository.save(smsHistory);
    }

    public void check(String phone, String code, AppLanguage lang) {
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phone);

        if (optional.isEmpty()) {
            log.warn("No sms history found for phone {}", phone);
            throw new AppBadException(resourceBundleMessageService.getMessage("sms.not.found", lang));
        }
        SmsHistoryEntity entity = optional.get();
        //setting attempt count to 0 if null
        int attemptCount = entity.getAttemptCount()==null ? 0 : entity.getAttemptCount();
        if(attemptCount>3){
            log.warn("Attempt count exceeds 3 limit for phone {}", phone);
            throw new AppBadException(resourceBundleMessageService.getMessage("too.many.attempts", lang));
        }
        //check code
        if (!entity.getCode().equals(code)) {
            smsHistoryRepository.updateAttemptCount(entity.getId());
            log.warn("Code mismatch for phone {}", phone);
            throw new AppBadException(resourceBundleMessageService.getMessage("no.matching.password", lang));
        }
        //check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if(LocalDateTime.now().isAfter(expDate)){
            log.warn("Verification expired for phone {}, sent time {}", phone,entity.getCreatedDate());
            throw new AppBadException(resourceBundleMessageService.getMessage("verification.time.expired", lang));
        }

    }
}
