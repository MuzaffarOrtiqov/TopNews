package api.kun.uz.dto.email_history;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class DateRequestDto {
    @NotNull(message = "Date is required")
    private LocalDate date;
}

