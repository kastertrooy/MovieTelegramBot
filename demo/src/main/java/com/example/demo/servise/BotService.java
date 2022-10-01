package com.example.demo.servise;

import com.example.demo.models.Action;
import com.example.demo.models.Comments;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BotService {
    private final BotLanguage botLanguage;
    private final Buttons buttons;
    private final MovieRepo movieRepo;
    private final UserRepo userRepo;
    //@Value("${file.Url}")
    private String fileUrl = "C:\\Users\\HP\\Desktop\\photos\\";
    private final Main mainBot;
    private final CommentService commentService;


    public BotService(BotLanguage botLanguage, Buttons buttons, MovieRepo movieRepo, UserRepo userRepo, Main mainBot, CommentService commentService) {
        this.botLanguage = botLanguage;
        this.buttons = buttons;
       this.movieRepo = movieRepo;
        this.userRepo = userRepo;
        this.mainBot = mainBot;
        this.commentService = commentService;
    }

    public User getUser(Long chatId){
        return userRepo.findByChatId(chatId);
    }
    public CurrentMessage handle(Update update) {
        String text=" ";
        CurrentMessage currentMessage = new CurrentMessage();
        if (update.hasMessage() && update.getMessage().hasText()){
            text = update.getMessage().getText();
        }
        else if (update.hasCallbackQuery()) {
            return callBack(update);
        }
        if (text.equals("/start")){
         return startBot(update.getMessage().getChatId());
        }
        if (List.of("O'zbekcha", "English", "Русский").contains(text)){
           botLanguage.setBotLanguage(text);
            return function(update.getMessage());
        }
        if (text.equals(botLanguage.buttonLanguage("searchMovie"))){
           return findMovie(update.getMessage().getChatId());
        }
        if (text.equals(botLanguage.buttonLanguage("allFilms"))){
            try {
              return getAllMovies(update.getMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (text.split(" ")[1].equals("->") || text.startsWith("<-")){
                try {
                    return getAllMovies(update.getMessage());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        if (text.equals(botLanguage.buttonLanguage("back"))){
           return  function(update.getMessage());
        }
        return currentMessage;
    }

    private CurrentMessage callBack(Update update) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        String text = update.getCallbackQuery().getData();
        if (text.startsWith("comments")){
             currentMessage = toCurrent(update.getCallbackQuery().getMessage().getChatId(),
                    botLanguage.text("сomments"));
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
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());

            pageRequest= PageRequest.of(page+1,10);
            comments = commentService.getAll(pageRequest,movieId);
            if (comments.isEmpty()){
                sendMessage.setReplyMarkup(buttons.addCommentAndWatch(movieId));
                try {
                    mainBot.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
          currentMessage.getSendMessage().setReplyMarkup(buttons.back());
            currentMessage.setType(MessageType.SendMessage);
            return currentMessage;
            }else if (text.startsWith("addComment")){
            User user = getUser(update.getCallbackQuery().getMessage().getChatId());
            if (user == null){
                user = new User();
              sendMessage.setText(botLanguage.text("profilNotFound")+"\n"+
                      botLanguage.text("pleaseRegistration")+"\n\n"+
                      botLanguage.text("sendEmail"));
              currentMessage.setSendMessage(sendMessage);
              currentMessage.setType(MessageType.SendMessage);
              user.setChatId(update.getCallbackQuery().getMessage().getChatId());
              user.setId(update.getCallbackQuery().getFrom().getId());
              user.setAction(Action.Registration_Email);
              userRepo.save(user);
              return currentMessage;
            }
            Integer movieId = Integer.valueOf(text.split("movieId:")[1].split("/")[0]);
        }
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
