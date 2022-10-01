package com.example.demo.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotLanguage {

    private String language="Русский";


    public void setBotLanguage(String language) {
        this.language = language;
    }


    public String text(String text) {
        switch (language){
            case "English"->{
                switch (text){
                    case "select function":return "Select function";
                    case "these are all movies":return "These are all movies";
                    case "searchFilms":return "Search movies";
                    case "сomments":return "Comments";
                    case "profilNotFound":return "Profil not fount";
                    case "pleaseRegistration":return "Please go through the registration";
                    case "sendEmail":return "Send your Email";

                }
            }
            case "O'zbekcha"->{
                switch (text){
                    case "select function":return "Funktsiyasiyani tanlang";
                    case "these are all movies":return "Shular barcha filmlar";
                    case "searchFilms":return "Kino qidirish";
                    case "сomments":return "Kommentariyalar";
                    case "profilNotFound":return "Profil topilmadi";
                    case "pleaseRegistration":return "Iltimos registratsiyadan o'ting";
                    case "sendEmail":return "Email-ingizni jonating";
                }
            }
            case "Русский"->{
                switch (text){
                    case "select function":return "Выбертие фунцию";
                    case "these are all movies":return "Это все фильмы";
                    case "searchFilms":return "Поиск фильмов";
                    case "сomments":return "Комментарии";
                    case "profilNotFound":return "Профиль не найден";
                    case "pleaseRegistration":return "Пожалуста пройдите регистрацию";
                    case "sendEmail":return "Отправте свой Email";
                }
            }
        }
        return "Error";
    }

    public String buttonLanguage(String text) {
        switch (language){
            case "English"->{
                switch (text){
                    case "searchMovie":return "Search movie";
                    case "allFilms":return "All films";
                    case "watch":return "Watch";
                    case "comments":return "Comments";
                    case "description":return "Description";
                    case "back":return "Back";
                    case "addComment":return "Add Comments";
                }
            }
            case "O'zbekcha"->{
                switch (text){
                    case "searchMovie":return "Filmni qidirish";
                    case "allFilms":return "Barch filmlar";
                    case "watch":return "Tomosha qilish";
                    case "comments":return "Kommentariyalar";
                    case "description":return "Janr";
                    case "back":return "Qaytish";
                    case "addComment":return "Komentariya qoshish";
                }
            }
            case "Русский"->{
                switch (text){
                    case "searchMovie":return "Поиск фильма";
                    case "allFilms":return "Все фильмы";
                    case "watch":return "Смотреть";
                    case "comments":return "Комментарии";
                    case "description":return "Жанр";
                    case "back":return "Назад";
                    case "addComment":return "Добавить коментарии";
                }
            }
        }
        return "Error";
    }
}
