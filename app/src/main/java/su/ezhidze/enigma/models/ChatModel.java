package su.ezhidze.enigma.models;


import java.util.List;

public class ChatModel {

    private Integer id;

    private List<UserResponseModel> users;

    private List<MessageResponseModel> messages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<UserResponseModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponseModel> users) {
        this.users = users;
    }

    public List<MessageResponseModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseModel> messages) {
        this.messages = messages;
    }
}
