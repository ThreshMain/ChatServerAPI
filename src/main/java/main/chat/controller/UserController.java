package main.chat.controller;

import main.chat.Password;
import main.chat.User;
import main.chat.repository.UserRepository;
import main.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "user")
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam String userName, @RequestParam(name = "password") String passwordRaw, @RequestParam(required = false) String color) {
        if (userService.findByUserName(userName).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name already taken");
        }
        User user = new User();
        user.setColor(color);
        user.setUserName(userName);
        Password userPassword = new Password();
        try {
            String passwordHash = Password.generatePasswordHash(passwordRaw);
            userPassword.setPasswordHash(passwordHash);
            user.setPassword(userPassword);
            userService.save(user);
            return "Saved ID:" + user.getId();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PostMapping(path = "/login")
    public @ResponseBody
    String login(@RequestParam String userName, @RequestParam(name = "password") String passwordRaw) {
        Optional<User> optionalUser = userService.findByUserName(userName);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                if (user.getPassword().verifyPassword(passwordRaw)) {
                    if (user.getToken() != null) {
                        return user.getToken();
                    } else {
                        boolean tokenTaken;
                        String token;
                        do {
                            token = UUID.randomUUID().toString();
                            Optional<User> userWithSameToken = userService.findByToken(token);
                            tokenTaken = userWithSameToken.isPresent();
                        } while (tokenTaken);
                        user.setToken(token);
                        userService.save(user);
                        return token;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userService.findAll();
    }
}
