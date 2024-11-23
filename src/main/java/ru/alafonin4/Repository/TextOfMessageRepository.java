package ru.alafonin4.Repository;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.repository.CrudRepository;
import ru.alafonin4.Entity.TextOfMessage;

import java.util.List;

public interface TextOfMessageRepository extends CrudRepository<TextOfMessage, Long> {
    List<TextOfMessage> findByType(String type);
}
