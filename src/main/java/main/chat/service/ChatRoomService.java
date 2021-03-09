package main.chat.service;

import main.chat.model.ChatRoom;
import main.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom save(ChatRoom chatRoom){
        return chatRoomRepository.save(chatRoom);
    }

    public Optional<ChatRoom> findById(int roomID) {
        return chatRoomRepository.findById(roomID);
    }

    public Iterable<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
}
