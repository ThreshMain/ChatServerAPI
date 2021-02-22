package main.chat;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 512, nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private User author;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private ChatRoom chatRoom;

    @JsonAlias(value = "roomID")
    public int getChatRoomID() {
        return chatRoom.getId();
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
