package com.example.demo.servise;

import com.example.demo.models.Movie;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
@Component
public class Buttons {
    private final BotLanguage language;

    public Buttons(BotLanguage language) {
        this.language = language;
    }

    ReplyKeyboard language() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        KeyboardButton uzbButton = new KeyboardButton();
        uzbButton.setText("O'zbekcha");
        row.add(uzbButton);

        KeyboardButton englishButton = new KeyboardButton();
        englishButton.setText("English");
        row.add(englishButton);


        KeyboardButton russianButton = new KeyboardButton();
        russianButton.setText("Русский");
        row.add(russianButton);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(row);
        replyKeyboardMarkup.setKeyboard(rowList);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboard function() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        
        KeyboardRow row = new KeyboardRow();
        
        KeyboardButton getMovie = new KeyboardButton();
        getMovie.setText(language.buttonLanguage("searchMovie"));
        row.add(getMovie);
        
        List<KeyboardRow>rowList = new ArrayList<>();
        rowList.add(row);
        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard allFilms() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow row = new KeyboardRow();
        KeyboardButton getMovie = new KeyboardButton();
        getMovie.setText(language.buttonLanguage("allFilms"));
        row.add(getMovie);
        List<KeyboardRow>rowList = new ArrayList<>();
        rowList.add(row);
        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard message(Movie movie) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            InlineKeyboardButton movieButton = new InlineKeyboardButton();
            movieButton.setText(movie.getName());
            movieButton.setCallbackData(movie.getId().toString());
            keyboardButtonsRow1.add(movieButton);
            rowList.add(keyboardButtonsRow1);
            keyboardButtonsRow1 = new ArrayList<>();

        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard watch(Movie movie) {
        List<InlineKeyboardButton>keyboardButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rowList = new ArrayList<>();

        InlineKeyboardButton watchButton = new InlineKeyboardButton();
        watchButton.setText(language.buttonLanguage("watch")+
                "▶ "+movie.getName());
        watchButton.setCallbackData("watch/movieId:"+movie.getId());
        keyboardButtons.add(watchButton);
        rowList.add(keyboardButtons);
        keyboardButtons = new ArrayList<>();

        InlineKeyboardButton descriptionButton = new InlineKeyboardButton();
        descriptionButton.setText(language.buttonLanguage("description")
                +": "+movie.getDescription());
        descriptionButton.setCallbackData("description/movieId:"+movie.getId()+"/");
        keyboardButtons.add(descriptionButton);
        rowList.add(keyboardButtons);
        keyboardButtons = new ArrayList<>();

        InlineKeyboardButton commentsButton = new InlineKeyboardButton();
        commentsButton.setText(language.buttonLanguage("comments"));
        commentsButton.setCallbackData("comments/movieId:"+movie.getId()+"/");
        keyboardButtons.add(commentsButton);
        rowList.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard addCommentAndWatch(Integer movieID) {
        List<InlineKeyboardButton>keyboardButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rowList = new ArrayList<>();

        InlineKeyboardButton watchButton = new InlineKeyboardButton();
        watchButton.setText(language.buttonLanguage("watch")+" ▶");
        watchButton.setCallbackData("watch/movieId:"+movieID.toString()+"/");
        keyboardButtons.add(watchButton);
        rowList.add(keyboardButtons);
        keyboardButtons = new ArrayList<>();

        InlineKeyboardButton commentsButton = new InlineKeyboardButton();
        commentsButton.setText(language.buttonLanguage("addComment")+" 📝");
        commentsButton.setCallbackData("addComment/movieId:"+movieID+"/");
        keyboardButtons.add(commentsButton);
        rowList.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard back() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton back = new KeyboardButton();
        back.setText(language.buttonLanguage("back"));
        keyboardRow.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard backAndPage(Integer page,Boolean right) {
        page+=1;
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardRow = new KeyboardRow();

        if (page >=2){
            KeyboardButton left = new KeyboardButton();
            left.setText(String.format("<- %s", page-1));
            keyboardRow.add(left);
        }
        KeyboardButton pageButton = new KeyboardButton();
        pageButton.setText(String.format("%s",page));
        keyboardRow.add(pageButton);

        if (right) {
            KeyboardButton rightButton = new KeyboardButton();
            rightButton.setText(String.format("%s ->", page + 1));
            keyboardRow.add(rightButton);
        }

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        keyboardRow = new KeyboardRow();

        KeyboardButton back = new KeyboardButton();
        back.setText(language.buttonLanguage("back"));
        keyboardRow.add(back);

        rowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard comments(Integer page, boolean b,Integer id) {
        List<InlineKeyboardButton>keyboardButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rowList = new ArrayList<>();

        if (page>=2 && false){
            InlineKeyboardButton leftButton = new InlineKeyboardButton();
            leftButton.setText("<- "+page.toString());
            leftButton.setCallbackData("comments/movieId:"+id+"/");
            keyboardButtons.add(leftButton);
        }
        InlineKeyboardButton pageButton = new InlineKeyboardButton();
        pageButton.setText(page.toString());
        keyboardButtons.add(pageButton);
        if (b & false){
            InlineKeyboardButton rightButton = new InlineKeyboardButton();
            rightButton.setText(page.toString()+"->");
            rightButton.setCallbackData("comments/movieId:"+id+"/");
            keyboardButtons.add(rightButton);
        }
        rowList.add(keyboardButtons);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard userOrAdmin() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton user = new KeyboardButton();
        user.setText(language.buttonLanguage("user"));
        keyboardRow.add(user);
        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        rowList = new ArrayList<>();

        KeyboardButton admin = new KeyboardButton();
        admin.setText(language.buttonLanguage("admin"));
        keyboardRow.add(admin);
        rowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard regOrLogin() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton registration = new KeyboardButton();
        registration.setText(language.buttonLanguage("registration"));
        keyboardRow.add(registration);
        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        rowList = new ArrayList<>();

        KeyboardButton login = new KeyboardButton();
        login.setText(language.buttonLanguage("login"));
        keyboardRow.add(login);
        rowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard empt() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton back = new KeyboardButton();
        back.setText(" ");
        keyboardRow.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(rowList);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard cofirmAndRename() {
        List<InlineKeyboardButton>keyboardButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rowList = new ArrayList<>();

        InlineKeyboardButton confirm = new InlineKeyboardButton();
        confirm.setText(language.buttonLanguage("confirm"));
        confirm.setCallbackData("confirm");
        keyboardButtons.add(confirm);
        rowList.add(keyboardButtons);
        keyboardButtons = new ArrayList<>();

        InlineKeyboardButton rename = new InlineKeyboardButton();
        rename.setText(language.buttonLanguage("rename"));
        rename.setCallbackData("rename");
        keyboardButtons.add(rename);
        rowList.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
