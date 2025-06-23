package api.kun.uz.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagResponseDto {
    private String id;
    private String tagName;
    private LocalDateTime createdDate;
    private Boolean visible;
}
