package main.chat.service;

import main.chat.User;
import main.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findByToken(String token){
        List<User> users= userRepository.findByToken(token);
        if(users.size()==1){
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }
    public Optional<User> findByUserName(String userName){
        List<User> users= userRepository.findByUserName(userName);
        if(users.size()==1){
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }
}
