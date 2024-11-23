package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "_user_answer")
@Getter
@Setter
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chatId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "test_session_id", referencedColumnName = "id")
    private UserTestSession testSession;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}

