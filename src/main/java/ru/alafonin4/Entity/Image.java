package ru.alafonin4.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "_image")
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "img", nullable = false)
    private byte[] img;

}
