package main.chat.controller;

import main.chat.model.ChatRoom;
import main.chat.model.Message;
import main.chat.model.User;
import main.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "message")
public class MessageController {


    private final UserService userService;

    @Autowired
    public MessageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Map<Integer, List<Message>> getAllMessages(@RequestParam String token) {
        Optional<User> optionalUser = userService.findByToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<Integer, List<Message>> messages = new HashMap<>();
            List<ChatRoom> chatRoomList = user.getChatRooms();
            for (ChatRoom room : chatRoomList) {
                messages.put(room.getId(), room.getMessages());
            }
            return messages;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }
}
