package su.ezhidze.enigma.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {

    private Integer id;

    private List<Message> messages;

    private List<User> users;

    public Chat() {
        messages = new ArrayList<>();
        users = new ArrayList<>();
    }

    public Chat(Integer id, List<Message> messages, List<User> users) {
        this.id = id;
        this.messages = messages;
        this.users = users;
    }
}
