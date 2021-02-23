package main.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Entity
@Table(indexes = {@Index(name = "IDX_MYIDX1", columnList = "id,token,userName")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 10, nullable = true)
    private String color;

    @Column(length = 50, nullable = false, unique = true)
    private String userName;

    @JsonIgnore
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Password password;

    @Column(unique = true)
    @JsonIgnore
    private String token;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private List<ChatRoom> chatRooms;

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
