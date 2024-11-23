package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.alafonin4.Entity.Word;

public interface WordRepository extends CrudRepository<Word, Long> {
}
