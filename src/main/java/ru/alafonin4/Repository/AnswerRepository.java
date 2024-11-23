package ru.alafonin4.Repository;

import ru.alafonin4.Entity.Answer;
import ru.alafonin4.Entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {
    List<Answer> findAllByQuestion(Question q);
}