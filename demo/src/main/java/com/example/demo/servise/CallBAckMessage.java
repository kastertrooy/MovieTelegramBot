package com.example.demo.servise;

import com.example.demo.models.Action;
import com.example.demo.models.Comments;
import com.example.demo.models.User;
import com.example.demo.repo.UserRepo;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class CallBAckMessage {
    private final Main main;
    private final Buttons buttons;
    private final BotLanguage botLanguage;
    private final UserRepo userRepo;
    private final CommentService commentService;

    public CallBAckMessage(Main main, Buttons buttons, BotLanguage botLanguage, UserRepo userRepo, CommentService commentService) {
        this.main = main;
        this.buttons = buttons;
        this.botLanguage = botLanguage;
        this.userRepo = userRepo;
        this.commentService = commentService;
    }
private boolean deleteMessage (Long chatId, Integer messageID){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageID);
    try {
        main.execute(deleteMessage);
        return true;
    } catch (TelegramApiException e) {
        throw new RuntimeException(e);
    }
}
    public CurrentMessage callBack(CallbackQuery callbackQuery) {
        CurrentMessage currentMessage = new CurrentMessage();
        currentMessage.setType(MessageType.SendMessage);
        SendMessage sendMessage = new SendMessage();
        sendMessage .setChatId(callbackQuery.getMessage().getChatId());
        String text = callbackQuery.getData();
        User user = userRepo.findByChatId(callbackQuery.getMessage().getChatId());
        if (user == null){
            sendMessage.setText(botLanguage.text("profilNotFound"));
            sendMessage.setReplyMarkup(buttons.empt());
            currentMessage.setSendMessage(sendMessage);
            return currentMessage;
        }
        Long chatId = callbackQuery.getMessage().getChatId();
        if (text.equals("confirm")){
            deleteMessage(chatId,callbackQuery.getMessage().getMessageId());
            if (user.getAction().equals(Action.Registration_Email)){
            user.setAction(Action.Registration_Password);
            }else if (user.getAction().equals(Action.Admin_Email)){
                user.setAction(Action.Admin_Password);
            }
            sendMessage.setText(botLanguage.text("sendPassword"));
            currentMessage.setSendMessage(sendMessage);
            userRepo.save(user);
            return currentMessage;
        }
        if (text.startsWith("comments")){
            sendMessage.setText(botLanguage.text("сomments"));
            currentMessage.setSendMessage(sendMessage);
            currentMessage.setType(MessageType.SendMessage);
            PageRequest pageRequest;
            Integer page;
            if (text.startsWith("comments/")){
                page = 0;
            }
            else {
                page = Integer.parseInt(text.split(" ")[1]);
            }
            Integer movieId = Integer.valueOf(text.split("movieId:")[1].split("/")[0]);
            pageRequest= PageRequest.of(page,10);
            Page<Comments> comments =  commentService.getAll(pageRequest,movieId);
            StringBuilder sendText = new StringBuilder();
            sendText.append(botLanguage.buttonLanguage("comments")).append("\n");
            for (Comments c:comments) {
                sendText.append("Id:").append(c.getId());
                sendText.append(c.getUser().getName()).append(" : ").append(c.getContent());
                sendText.append("\n");
            }

            sendMessage.setText(String.valueOf(sendText));
            sendMessage.setChatId(callbackQuery.getMessage().getChatId());

            pageRequest= PageRequest.of(page+1,10);
            comments = commentService.getAll(pageRequest,movieId);
            if (comments.isEmpty()){
                sendMessage.setReplyMarkup(buttons.addCommentAndWatch(movieId));
                try {
                    main.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            currentMessage.getSendMessage().setReplyMarkup(buttons.back());
            currentMessage.setType(MessageType.SendMessage);
            return currentMessage;
        }else if (text.startsWith("addComment")){
            user = userRepo.findByChatId(callbackQuery.getMessage().getChatId());
            if (user == null){
                user = new User();
                sendMessage.setText(botLanguage.text("profilNotFound")+"\n"+
                        botLanguage.text("pleaseRegistration")+"\n\n"+
                        botLanguage.text("sendEmail"));
                currentMessage.setSendMessage(sendMessage);
                currentMessage.setType(MessageType.SendMessage);
                user.setChatId(callbackQuery.getMessage().getChatId());
                user.setAction(Action.Registration_Email);
                userRepo.save(user);
                return currentMessage;
            }
            Integer movieId = Integer.valueOf(text.split("movieId:")[1].split("/")[0]);
        }
        return currentMessage;
    }
}
