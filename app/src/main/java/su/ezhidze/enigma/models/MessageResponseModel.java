package su.ezhidze.enigma.models;

public class MessageResponseModel {

    private Integer id;

    private Integer chatId;

    private String messageText;

    public MessageResponseModel(final Message message) {
        chatId = message.getChatId();
        messageText = message.getMessageText();
    }
}
