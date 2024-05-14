package su.ezhidze.enigma.models;


import java.io.Serializable;

public class Message implements Serializable {

    private Integer chatId;

    private String messageText;

    private String senderSubject;

    public Message(Integer chatId, String messageText) {
        this.chatId = chatId;
        this.messageText = messageText;
    }

    public Message(final InputOutputMessageModel inputOutputMessageModel) {
        chatId = inputOutputMessageModel.getChatId();
        messageText = inputOutputMessageModel.getMessageText();
        senderSubject = inputOutputMessageModel.getSenderSubject();
    }

    public Message(final MessageResponseModel messageResponseModel) {
        chatId = messageResponseModel.getChatId();
        messageText = messageResponseModel.getMessageText();
        senderSubject = messageResponseModel.getSenderSubject();
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
