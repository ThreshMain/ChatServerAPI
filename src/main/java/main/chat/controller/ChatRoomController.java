package main.chat.controller;

import main.chat.ChatRoom;
import main.chat.Message;
import main.chat.Password;
import main.chat.User;
import main.chat.service.ChatRoomService;
import main.chat.service.MessageService;
import main.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "room")
public class ChatRoomController {


    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/add")
    public @ResponseBody
    String addNewChatRoom(@RequestParam(name = "password", required = false) String passwordRaw) {
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

    @PostMapping(path = "/{roomID}/join")
    public String joinChatRoom(@PathVariable("roomID") int roomID, @RequestParam String token, @RequestParam(name = "password", required = false) String passwordRaw) {
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

    @PostMapping(path = "/{roomID}/send")
    public @ResponseBody
    String sendNewMessage(@PathVariable("roomID") int roomID, @RequestParam String content, @RequestParam String token) {
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

    @GetMapping(path = "/{roomID}/get")
    public @ResponseBody
    Iterable<Message> getAllMessages(@PathVariable("roomID") int roomID, @RequestParam String token) {
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

    @GetMapping(path = "/{roomID}/{messageId}/get")
    public @ResponseBody
    Message getMessage(@PathVariable("roomID") int roomID, @PathVariable("messageId") int messageId,@RequestParam String token) {
        Optional<ChatRoom> optionalRoom = chatRoomService.findById(roomID);
        Optional<User> optionalUser = userService.findByToken(token);
        if (optionalRoom.isPresent()) {
            if (optionalUser.isPresent()) {
                ChatRoom room = optionalRoom.get();
                User user = optionalUser.get();
                if (!room.isLocked() || room.getUsers().contains(user)) {
                    List<Message> messageList=room.getMessages();
                    for(Message message:messageList){
                        if(message.getId()==messageId){
                            return message;
                        }
                    }
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message not found");

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
