package su.ezhidze.enigma.models;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private Integer id;

    private List<Message> messages;

    public Chat() {
        messages = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
