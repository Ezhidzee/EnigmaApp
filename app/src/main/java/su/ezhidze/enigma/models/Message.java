package su.ezhidze.enigma.models;


public class Message {

    private Integer chatId;

    private String messageText;

    public Message(Integer chatId, String messageText) {
        this.chatId = chatId;
        this.messageText = messageText;
    }

    public Message(final InputOutputMessageModel inputOutputMessageModel) {
        chatId = inputOutputMessageModel.getChatId();
        messageText = inputOutputMessageModel.getMessageText();
    }

    public Message(final MessageResponseModel messageResponseModel) {
        chatId = messageResponseModel.getChatId();
        messageText = messageResponseModel.getMessageText();
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
