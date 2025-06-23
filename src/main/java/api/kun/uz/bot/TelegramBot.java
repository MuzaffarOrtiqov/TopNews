package api.kun.uz.bot;


import api.kun.uz.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public class TelegramBot extends TelegramLongPollingBot {

    private final String botName;

    public TelegramBot(String botName, String botToken) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update updates) {

    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }


}
