package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private Integer chatId;

    private String messageText;

    public Message(Integer chatId, String messageText) {
        this.chatId = chatId;
        this.messageText = messageText;
    }
}
