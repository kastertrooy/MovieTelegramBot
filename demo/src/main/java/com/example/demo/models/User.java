package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter


@Entity
@Table(name = ("users"))
public class User {
    @Id
    private Long chatId;
    private Long id;
    private String name;
    private String email;
    private String password;
    private Integer age;
    private Boolean status;
    private Action action;


}
