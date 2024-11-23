package ru.alafonin4.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "_answer")
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "is_right", nullable = false)
    private Boolean isRight;
}