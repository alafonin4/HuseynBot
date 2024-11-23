package ru.alafonin4.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.alafonin4.Entity.CurrentLanguage;
import ru.alafonin4.Entity.User;

public interface CurrentLanguageRepository extends CrudRepository<CurrentLanguage, Long> {
    CurrentLanguage findByUser(User user);
}
