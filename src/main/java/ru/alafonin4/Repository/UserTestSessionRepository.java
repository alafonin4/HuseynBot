package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alafonin4.Entity.UserTestSession;

@Repository
public interface UserTestSessionRepository  extends CrudRepository<UserTestSession, Long> {
}
