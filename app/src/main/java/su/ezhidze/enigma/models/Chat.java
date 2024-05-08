package su.ezhidze.enigma.models;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private Integer id;

    private List<Message> messages;

    private List<User> users;

    public Chat() {
        messages = new ArrayList<>();
        users = new ArrayList<>();
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Chat(Integer id, List<Message> messages, List<User> users) {
        this.id = id;
        this.messages = messages;
        this.users = users;
    }
}
