package main.chat.controller;

import main.chat.ChatRoom;
import main.chat.Password;
import main.chat.User;
import main.chat.service.ChatRoomService;
import main.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RestController
@RequestMapping(value = "room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam(name = "password", required = false) String passwordRaw) {
        ChatRoom chatRoom = new ChatRoom();
        if (passwordRaw != null) {
            Password roomPassword = new Password();
            try {
                String passwordHash = Password.generatePasswordHash(passwordRaw);
                roomPassword.setPasswordHash(passwordHash);
                chatRoom.setPassword(roomPassword);
                chatRoomService.save(chatRoom);
                return "Saved ID:" + chatRoom.getId();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return "Something went wrong " + e.getMessage();
            }
        } else {
            chatRoomService.save(chatRoom);
            return "Saved ID:" + chatRoom.getId();
        }
    }

    @PostMapping(path = "/join/{id}")
    public String joinChatRoom(@PathVariable("id") int roomID, @RequestParam String token, @RequestParam(name = "password", required = false) String passwordRaw) {
        Optional<ChatRoom> optionalRoom = chatRoomService.findById(roomID);
        Optional<User> optionalUser = userService.findByToken(token);
        if (optionalRoom.isPresent()) {
            if (optionalUser.isPresent()) {
                ChatRoom room = optionalRoom.get();
                User user = optionalUser.get();
                if (room.isLocked()) {
                    try {
                        if (passwordRaw != null && room.getPassword().verifyPassword(passwordRaw)) {
                            return joinChatRoom(room, user);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
                        }
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                } else {
                    return joinChatRoom(room, user);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }
    }

    private String joinChatRoom(ChatRoom room, User user) {
        room.getUsers().add(user);
        user.getChatRooms().add(room);
        userService.save(user);
        chatRoomService.save(room);
        return "Successfully joined chat room";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<ChatRoom> getAllUsers() {
        return chatRoomService.findAll();
    }
}
