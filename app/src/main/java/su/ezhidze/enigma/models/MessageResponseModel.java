package su.ezhidze.enigma.models;

public class MessageResponseModel {

    private Integer id;

    private Integer chatId;

    private String messageText;

    public MessageResponseModel(final Message message) {
        chatId = message.getChatId();
        messageText = message.getMessageText();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
