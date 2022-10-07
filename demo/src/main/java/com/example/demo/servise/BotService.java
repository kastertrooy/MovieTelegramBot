package com.example.demo.servise;

import com.example.demo.models.Action;
import com.example.demo.models.Movie;
import com.example.demo.models.User;
import com.example.demo.repo.MovieRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class BotService {
    private final Main mainBot;
    private final Buttons buttons;
    private final UserRepo userRepo;
    private final MovieRepo movieRepo;
    private final BotLanguage botLanguage;


    public BotService(BotLanguage botLanguage,
                      Buttons buttons, MovieRepo
                              movieRepo, UserRepo
                              userRepo, Main mainBot) {
        this.botLanguage = botLanguage;
        this.buttons = buttons;
       this.movieRepo = movieRepo;
        this.userRepo = userRepo;
        this.mainBot = mainBot;
    }

    public CurrentMessage handle(Message message) {
        String text=" ";
        Long chatId = message.getChatId();
        CurrentMessage currentMessage = new CurrentMessage();
        if (message.hasText()){
            text = message.getText();
        }
        checkReg(message);
        if (text.equals("/start")){
         return startBot(message.getChatId());
        }
        if (List.of("O'zbekcha", "English", "Русский").contains(text)){
           botLanguage.setBotLanguage(text);
            return userOtAdmin(message);
        }
        if (text.equals(botLanguage.buttonLanguage("user"))){
            return regOrLogin(chatId);
        }
        if (text.equals(botLanguage.buttonLanguage("registration"))){
            return userReg(chatId);
        }
        if (text.equals(botLanguage.buttonLanguage("searchMovie"))){
           return findMovie(message.getChatId());
        }
        if (text.equals(botLanguage.buttonLanguage("allFilms"))){
            try {
              return getAllMovies(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (text.split(" ")[1].equals("->") || text.startsWith("<-")){
                try {
                    return getAllMovies(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        if (text.equals(botLanguage.buttonLanguage("back"))){
           return  function(message);
        }
        return currentMessage;
    }

    private boolean checkReg(Message message) {
        User user = userRepo.findByChatId(message.getChatId());
        if (user == null){
            return false;
        }
        if (!message.hasText()){
            return false;
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        String text = message.getText();
        if (user.getAction().equals(Action.Registration_Email)){
            if (!text.toLowerCase().endsWith("@gmail.com")){
                sendMessage.setText(botLanguage.text("sureEmail")+"\n( "+
                        text+" )");
                mainBot.executMessagE(sendMessage);
                return false;
            }
            user.setEmail(text);
            sendMessage.setText(botLanguage.text("confirmEmail")+
                    "\n( "+ text+" )");
            sendMessage.setReplyMarkup(buttons.cofirmAndRename());
            mainBot.executMessagE(sendMessage);
            userRepo.save(user);
            return true;
        }else if (user.getAction().equals(Action.Registration_Password)){
            if (text.length() < 8 || text.length() > 240){
                sendMessage.setText(botLanguage.text("passwordSize"));
                mainBot.executMessagE(sendMessage);
                return false;
            }
            user.setPassword(text);
            userRepo.save(user);
            return true;
        }else if (user.getAction().equals(Action.Registration_Name)){
            sendMessage.setText(botLanguage.text("confirmName")
            +"\n( " + text + " )");
            sendMessage.setReplyMarkup(buttons.cofirmAndRename());
            mainBot.executMessagE(sendMessage);
            return true;
        }else if (user.getAction().equals(Action.Registration_Age)){
            int age = Integer.parseInt(text);
            if (age > 10 && age < 120){
                user.setAge(age);
                userRepo.save(user);
                return true;
            }
            sendMessage.setReplyMarkup(buttons.empt());
            mainBot.executMessagE(sendMessage);
            sendMessage.setText("pleaseAge");
            return false;
        }
        return false;
    }

    private CurrentMessage userReg(Long chatId) {
        CurrentMessage currentMessage = new CurrentMessage();
        currentMessage.setType(MessageType.SendMessage);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        newUser(chatId,Action.Registration_Email);
        sendMessage.setText(botLanguage.text("sendEmail"));
        sendMessage.setReplyMarkup(buttons.empt());
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }
    private User newUser(Long chatId,Action action){
        User user = new User();
        user.setAction(action);
        user.setChatId(chatId);
        userRepo.save(user);
        return user;
    }

    private CurrentMessage regOrLogin(Long chatId) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(botLanguage.text("regOrLogin"));
        sendMessage.setReplyMarkup(buttons.regOrLogin());
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setType(MessageType.SendMessage);
        return currentMessage;
    }

    private CurrentMessage userOtAdmin(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(buttons.userOrAdmin());
        sendMessage.setText(botLanguage.text("role"));
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setType(MessageType.SendMessage);
        return currentMessage;
    }


    private CurrentMessage getAllMovies(Message message) throws TelegramApiException {
        CurrentMessage currentMessage = toCurrent(message.getChatId(),
                botLanguage.text("these are all movies"));
        Integer page = 0;
        String text = message.getText();
          if (text.startsWith("<-")) {
            page = Integer.parseInt(text.split("<- ")[1])-1;
        } else if (text.split(" ")[1].equals("->")) {
            page = Integer.parseInt(text.split(" ")[0])-1;
        }
        PageRequest pageRequest = PageRequest.of(page, 2);
        Page<Movie> patients = movieRepo.findAll(pageRequest);
        List<Movie>moviesList = new ArrayList<>();
        for (Movie m:patients) {
            moviesList.add(m);
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        for (Movie m:
             moviesList) {
            //@Value("${file.Url}")
            String fileUrl = "C:\\Users\\HP\\Desktop\\photos\\";
            Path path = Paths.get(fileUrl).resolve(m.getImageURL().split("localhost:8080/api/v1/movies/get/")[1]);
            File file = new File(path.toUri());
            InputFile inputFile = new InputFile().setMedia(file);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setReplyMarkup(buttons.watch(m));
            mainBot.execute(sendPhoto);
        }
        pageRequest = PageRequest.of(page+1, 2);
        Page<Movie> test = movieRepo.findAll(pageRequest);
        if (test.hasContent()){
            currentMessage.getSendMessage().setReplyMarkup(buttons.backAndPage(page, true));
            return currentMessage;
        }
        currentMessage.getSendMessage().setReplyMarkup(buttons.backAndPage(page,false));
        return currentMessage;
    }

    private CurrentMessage findMovie(Long chatId) {

        CurrentMessage currentMessage = toCurrent(chatId,
                botLanguage.text("searchFilms"));
        currentMessage.getSendMessage().setReplyMarkup(buttons.allFilms());
        return currentMessage;
    }

    private CurrentMessage function(Message message) {
        CurrentMessage currentMessage = toCurrent(message.getChatId(),
                botLanguage.text("select function"));
        currentMessage.getSendMessage().setReplyMarkup(buttons.function());
return currentMessage;
    }
private CurrentMessage toCurrent(Long chatId,String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        CurrentMessage currentMessage = new CurrentMessage();
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setType(MessageType.SendMessage);
        return currentMessage;
}
    private CurrentMessage startBot(Long chatId) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttons.language());
        sendMessage.setText("tilni tanlang");
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setType(MessageType.SendMessage);
        return currentMessage;
    }


}
