package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alafonin4.Entity.Role;
import ru.alafonin4.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUserName(String messageText);

    List<User> getAllByRole(Role moderator);
}
