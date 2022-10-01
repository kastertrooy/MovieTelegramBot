package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity@Table
public class Comments {
    @Id
    private Integer id;
    private String content;
    @ManyToOne
    private Movie movie;
    @OneToOne
    private User user;

}
