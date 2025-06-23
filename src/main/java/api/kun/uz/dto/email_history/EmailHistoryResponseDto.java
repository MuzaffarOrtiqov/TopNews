package api.kun.uz.dto.email_history;

import api.kun.uz.enums.EmailType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailHistoryResponseDto {
    private String id;
    private String email;
    private String message;
    private String code;
    private EmailType emailType;
    private LocalDateTime createdDate;
}
