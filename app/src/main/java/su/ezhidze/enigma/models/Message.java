package su.ezhidze.enigma.models;


public class Message {

    private Chat chat;

    private String messageText;

    public Message(Integer id, Chat chat, String messageText) {
        this.chat = chat;
        this.messageText = messageText;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
