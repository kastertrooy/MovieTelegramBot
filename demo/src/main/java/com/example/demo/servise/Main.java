package com.example.demo.servise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Main extends TelegramLongPollingBot {
    @Autowired
    @Lazy
    private BotService botServise;
    @Value("${telegram.token}")
    private String token;
    @Value("${telegram.nickName}")
    private String nickName;



    @Override
    public String getBotUsername() {
        return nickName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        CurrentMessage message;
            message = botServise.handle(update);

        if (message != null && message.getType() != null) {
            executMessage(message);
        }

    }

    public void executMessage(CurrentMessage message) {
        MessageType type = message.getType();
        if (type.equals(MessageType.SendMessage)) {
            try {
                execute(message.getSendMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        if (type.equals(MessageType.SendPhoto)) {
            try {
                execute(message.getSendPhoto());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
