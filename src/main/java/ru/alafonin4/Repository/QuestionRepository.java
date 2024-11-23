package ru.alafonin4.Repository;

import ru.alafonin4.Entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {
    List<Question> findByDifficulty(int difficulty);
}