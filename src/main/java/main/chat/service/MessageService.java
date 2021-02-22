package main.chat.service;

import main.chat.Message;
import main.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message save(Message message){
        return messageRepository.save(message);
    }
}
