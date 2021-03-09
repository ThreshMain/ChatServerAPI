package main.chat.controller;

import main.chat.model.Password;
import main.chat.TokenGenerator;
import main.chat.model.User;
import main.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RestController
@RequestMapping(value = "user")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(path = "/register")
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
                            token = TokenGenerator.getToken();
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


    @GetMapping(path = "/{id}/get")
    public @ResponseBody
    User getUserWithId(@PathVariable("id") int id) {
        Optional<User> user = userService.findById(id);
        return user.orElse(null);
    }
}
