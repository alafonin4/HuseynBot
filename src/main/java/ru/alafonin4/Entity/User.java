package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity(name = "_user")
@Getter
@Setter
public class User {
    @Id
    @Column(name = "chatId", nullable = false)
    private Long chatId;

    @Column(name = "firstName", nullable = false)
    private String name;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "stageOfUs", nullable = true)
    private String stageOfUs;
}
