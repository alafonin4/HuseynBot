package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "_word")
@Getter
@Setter
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(name = "English", nullable = false)
    private String wordInEnglish;

    @Column(name = "Transcription", nullable = true)
    private String transcription;

    @Column(name = "Russian", nullable = false)
    private String wordInRussian;

    @Column(name = "Tajik", nullable = false)
    private String wordInTajik;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
