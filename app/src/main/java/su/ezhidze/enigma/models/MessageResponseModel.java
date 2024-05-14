package su.ezhidze.enigma.models;

public class MessageResponseModel {

    private Integer id;

    private Integer chatId;

    private String messageText;

    private String senderSubject;

    public MessageResponseModel(final Message message) {
        chatId = message.getChatId();
        messageText = message.getMessageText();
        senderSubject = message.getSenderSubject();
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

    public String getSenderSubject() {
        return senderSubject;
    }

    public void setSenderSubject(String senderSubject) {
        this.senderSubject = senderSubject;
    }
}
