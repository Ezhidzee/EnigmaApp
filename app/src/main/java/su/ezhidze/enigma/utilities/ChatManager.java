package su.ezhidze.enigma.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import su.ezhidze.enigma.activities.ConversationActivity;
import su.ezhidze.enigma.activities.MainActivity;
import su.ezhidze.enigma.exceptions.RecordNotFoundException;
import su.ezhidze.enigma.fragments.ChatsFragment;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.InputOutputMessageModel;
import su.ezhidze.enigma.models.Message;
import su.ezhidze.enigma.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatManager {

    private ArrayList<Chat> chatList;

    private PreferenceManager preferenceManager;

    private final Gson gson;

    public ChatManager(PreferenceManager pM) {
        preferenceManager = MainActivity.preferenceManager;
        gson = new Gson();
        chatList = new ArrayList<>();
        if (MainActivity.preferenceManager.getString(Constants.KEY_CHATS) != null) {
            chatList = gson.fromJson(MainActivity.preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        } else MainActivity.preferenceManager.putString(Constants.KEY_CHATS, gson.toJson(chatList));
    }

    public ArrayList<Chat> getChatList() {
        return getChatsFromPrefs();
    }

    public void addChat(Chat chat) {
        chatList.add(chat);
        save();
    }

    public void addMessage(InputOutputMessageModel message) {
        boolean isFound = false;
        for (Chat chat : chatList) {
            if (Objects.equals(chat.getId(), message.getChatId())) {
                isFound = true;
                chat.getMessages().add(new Message(message));
                save();
                ChatsFragment.updateData();
                if (ConversationActivity.getChat() != null && chat.getId().equals(ConversationActivity.getChat().getId())) {
                    ConversationActivity.updateData();
                }
                break;
            }
        }
        if (!isFound) throw new RecordNotFoundException("Chat not found");
    }

    public Chat getChatById(Integer chatId) {
        boolean isFound = false;
        for (Chat chat : chatList) {
            if (Objects.equals(chat.getId(), chatId)) {
                isFound = true;
                return chat;
            }
        }
        if (!isFound) throw new RecordNotFoundException("Chat not found");
        return null;
    }

    public void deleteChat(Integer chatId) {
        getChatsFromPrefs();
        boolean isFound = false;
        int position = 0;
        for (int i = 0; i < chatList.size(); i++) {
            if (Objects.equals(chatList.get(i).getId(), chatId)) {
                isFound = true;
                position = i;
            }
        }
        if (!isFound) {
            throw new RecordNotFoundException("Chat not found");
        } else {
            chatList.remove(position);
            save();
            ChatsFragment.updateData();
        }
    }

    public void setChatUsers(Integer chatId, List<User> users) {
        getChatById(chatId).setUsers(users);
        save();
        ChatsFragment.updateData();
        if (ConversationActivity.getChat() != null && chatId.equals(ConversationActivity.getChat().getId())) {
            ConversationActivity.updateData();
        }
    }

    private void save() {
        String json = gson.toJson(chatList, new TypeToken<ArrayList<Chat>>() {}.getType());
        MainActivity.preferenceManager.putString(Constants.KEY_CHATS, json);
    }

    private ArrayList<Chat> getChatsFromPrefs() {
        chatList = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        return chatList;
    }
}
