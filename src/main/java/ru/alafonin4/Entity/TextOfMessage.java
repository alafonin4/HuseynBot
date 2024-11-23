package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "_text")
@Getter
@Setter
public class TextOfMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "text_in_russian", columnDefinition = "TEXT")
    private String textInRussian;

    @Column(name = "text_in_tajik", columnDefinition = "TEXT")
    private String textInTajik;
}
