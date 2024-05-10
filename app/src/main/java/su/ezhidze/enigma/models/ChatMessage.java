package su.ezhidze.enigma.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private String senderId;

    private String receiverId;

    private String message;

    private String dateTime;

    private Date dateObject;

    private String conversationId;

    private String conversationName;

    private String conversationImage;
}
