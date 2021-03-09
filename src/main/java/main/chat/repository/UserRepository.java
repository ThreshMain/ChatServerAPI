package main.chat.repository;

import main.chat.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    List<User> findByUserName(String userName);
    List<User> findByToken(String token);
}