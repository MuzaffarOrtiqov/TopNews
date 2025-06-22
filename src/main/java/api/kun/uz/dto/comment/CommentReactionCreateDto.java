package api.kun.uz.dto.comment;

import api.kun.uz.enums.Reaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReactionCreateDto {
    @NotBlank(message = "CommentId is required")
    private String commentId;
    @NotNull(message = "Reaction is required")
    private Reaction reaction;
}
