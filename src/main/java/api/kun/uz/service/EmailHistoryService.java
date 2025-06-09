package api.kun.uz.service;

import api.kun.uz.entity.EmailHistoryEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.enums.EmailType;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.EmailHistoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class EmailHistoryService {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;

    public Long getEmailCount(String email) {
       return emailHistoryRepository.getEmailCount(email, EmailType.REGISTRATION);
    }

    public void createEmailHistory(String username, String subject, String body, String code, EmailType emailType) {
        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setEmail(username);
        emailHistoryEntity.setSubject(subject);
        emailHistoryEntity.setBody(body);
        emailHistoryEntity.setCode(code);
        emailHistoryEntity.setAttemptCount(0l);
        emailHistoryEntity.setEmailType(emailType);
        emailHistoryRepository.save(emailHistoryEntity);
    }

    public void check(String username,String confirmCode, AppLanguage lang) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(username);
        if (optional.isEmpty()) {
            log.info("Email history does not exist with email: {}", username);
            throw new AppBadException(resourceBundleMessageService.getMessage("email.not.found", lang));
        }
        EmailHistoryEntity entity = optional.get();
        if (entity.getAttemptCount() > 3) {
            log.warn("Attempt count exceeds 3: {}", entity.getAttemptCount());
            throw new AppBadException(resourceBundleMessageService.getMessage("too.many.attempts", lang));
        }
        //check code
        if (!entity.getCode().equals(confirmCode)) {
            emailHistoryRepository.updateAttemptCount(entity.getId());
            log.info("Wrong password : email {}, saved code {}, provided code {} ", username, entity.getCode(), confirmCode);
            throw new AppBadException(resourceBundleMessageService.getMessage("no.matching.password", lang));
        }
        //check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(10);
        if (LocalDateTime.now().isAfter(expDate)) {
            log.warn("Sent email confirmation time expired : email {}, sent time: {}", username, entity.getCreatedDate()) ;
            throw new AppBadException(resourceBundleMessageService.getMessage("verification.time.expired", lang));
        }
    }
}
