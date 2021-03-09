import main.Main;
import main.chat.service.ChatRoomService;
import main.chat.service.MessageService;
import main.chat.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ChatServerTest {
    @Autowired
    UserService userService;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    MessageService messageService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void notNull() {
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(chatRoomService);
        Assertions.assertNotNull(messageService);
    }

    @Test
    public void testGetRooms() throws Exception {
        Random random=new Random();
        this.mockMvc.perform(get("/room/all")).andDo(print()).andExpect(status().isOk());
    }
}