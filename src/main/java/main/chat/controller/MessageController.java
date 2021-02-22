package main.chat.controller;

import main.chat.ChatRoom;
import main.chat.Message;
import main.chat.User;
import main.chat.service.ChatRoomService;
import main.chat.service.MessageService;
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

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/send/{id}")
    public @ResponseBody
    String sendNewMessage(@PathVariable("id") int roomID, @RequestParam String content, @RequestParam String token) {
        Optional<ChatRoom> optionalRoom = chatRoomService.findById(roomID);
        Optional<User> optionalUser = userService.findByToken(token);
        if (optionalRoom.isPresent()) {
            if (optionalUser.isPresent()) {
                ChatRoom room = optionalRoom.get();
                User user = optionalUser.get();
                if (!room.isLocked() || room.getUsers().contains(user)) {
                    Message message = new Message();
                    message.setContent(content);
                    message.setAuthor(user);
                    message.setChatRoom(room);
                    messageService.save(message);
                    room.sendMessage(message);
                    chatRoomService.save(room);
                    return "Message sent";
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have access to this room!");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }
    }

    @GetMapping(path = "/all/{id}")
    public @ResponseBody
    Iterable<Message> getAllMessages(@PathVariable("id") int roomID, @RequestParam String token) {
        Optional<ChatRoom> optionalRoom = chatRoomService.findById(roomID);
        Optional<User> optionalUser = userService.findByToken(token);
        if (optionalRoom.isPresent()) {
            if (optionalUser.isPresent()) {
                ChatRoom room = optionalRoom.get();
                User user = optionalUser.get();
                if (!room.isLocked() || room.getUsers().contains(user)) {
                    return room.getMessages();
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have access to this room!");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }
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
