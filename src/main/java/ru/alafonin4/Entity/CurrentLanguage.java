package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "_current_language")
@Getter
@Setter
public class CurrentLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chatId")
    private User user;

    @Column(name = "language", nullable = true)
    private String language;
}
