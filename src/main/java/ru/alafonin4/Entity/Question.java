package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "_question")
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "difficulty", nullable = false)
    private Integer difficulty;
}
