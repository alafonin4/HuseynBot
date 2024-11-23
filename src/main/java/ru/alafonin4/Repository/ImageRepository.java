package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.alafonin4.Entity.Answer;
import ru.alafonin4.Entity.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
}
