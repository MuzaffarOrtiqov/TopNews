package api.kun.uz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramNotificationService {
    private final AbsSender sender;

    @Value("${telegram.group_id}")
    private String groupChatId;

    public TelegramNotificationService(AbsSender sender) {
        this.sender = sender;
    }

    public void sendCommentToGroup(String articleTitle, String commenter, String commentText) {
        String message = """
                📰 *New Comment on Article:*
                *Article:* %s
                *By:* %s
                *Comment:* %s
                """.formatted(articleTitle, commenter, commentText);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("Markdown");

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
