package su.ezhidze.enigma.models;

import java.util.List;
import java.util.stream.Collectors;

public class ChatModel {

    private Integer id;

    private List<UserResponseModel> users;

    private List<MessageResponseModel> messages;

    public ChatModel(final Chat chat) {
        id = chat.getId();
        users = chat.getUsers().stream().map(UserResponseModel::new).collect(Collectors.toList());
        messages = chat.getMessages().stream().map(MessageResponseModel::new).collect(Collectors.toList());
    }
}
