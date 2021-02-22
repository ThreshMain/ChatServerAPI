/*
// Testing ONLY
package main.chat.controllers;

import main.chat.Password;
import main.chat.repositorie.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(value = "password")
public class PasswordController {

    @Autowired
    private PasswordRepository passwordRepository;


    @PostMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam(name = "password") String passwordRaw) {
        Password password = new Password();
        try {
            String passwordHash = Password.generatePasswordHash(passwordRaw);
            password.setPasswordHash(passwordHash);
            passwordRepository.save(password);
            return "Saved ID:" + password.getId();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return "Something went wrong " + e.getMessage();
        }
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<Password> getAllUsers() {
        return passwordRepository.findAll();
    }
}
*/