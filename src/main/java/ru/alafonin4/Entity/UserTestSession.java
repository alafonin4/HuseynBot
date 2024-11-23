package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "_user_test_session")
@Getter
@Setter
public class UserTestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chatId")
    private User user;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "testSession", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserAnswer> userAnswers;  // Список ответов в рамках одного теста
}

