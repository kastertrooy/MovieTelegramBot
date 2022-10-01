package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString

@Entity
@Table

public class Movie {
    @Id
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime deleteAt;
    private String imageURL;
}

