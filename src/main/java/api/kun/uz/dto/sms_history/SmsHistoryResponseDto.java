package api.kun.uz.dto.sms_history;

import api.kun.uz.enums.SmsType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsHistoryResponseDto {
    private String id;
    private String phoneNumber;
    private String message;
    private String code;
    private SmsType smsType;
    private LocalDateTime createdDate;
}
