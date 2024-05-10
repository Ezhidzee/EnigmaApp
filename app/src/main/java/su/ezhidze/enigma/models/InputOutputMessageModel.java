package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputOutputMessageModel {

    private String senderSubject;

    private Integer chatId;

    private String messageText;

    public InputOutputMessageModel(String senderSubject, Integer chatId, String messageText) {
        this.senderSubject = senderSubject;
        this.chatId = chatId;
        this.messageText = messageText;
    }

    public InputOutputMessageModel() {
    }
}
