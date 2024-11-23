package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alafonin4.Entity.Question;
import ru.alafonin4.Entity.UserAnswer;

@Repository
public interface UserAnswerRepository extends CrudRepository<UserAnswer, Long> {
}
