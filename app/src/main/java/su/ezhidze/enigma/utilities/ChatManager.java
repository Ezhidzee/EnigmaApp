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

import java.util.ArrayList;
import java.util.Objects;

public class ChatManager {

    private ArrayList<Chat> chats;

    private PreferenceManager preferenceManager;

    private final Gson gson;

    public ChatManager(PreferenceManager pM) {
        preferenceManager = MainActivity.preferenceManager;
        gson = new Gson();
        chats = new ArrayList<>();
        if (MainActivity.preferenceManager.getString(Constants.KEY_CHATS) != null) {
            chats = gson.fromJson(MainActivity.preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        } else MainActivity.preferenceManager.putString(Constants.KEY_CHATS, gson.toJson(chats));
    }

    public ArrayList<Chat> getChats() {
        return getChatsFromPrefs();
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        save();
    }

    public void addMessage(InputOutputMessageModel message) {
        boolean isFound = false;
        for (Chat chat : chats) {
            if (Objects.equals(chat.getId(), message.getChatId())) {
                isFound = true;
                chat.getMessages().add(new Message(message));
                save();
                ChatsFragment.updateData();
                if (chat.getId().equals(ConversationActivity.getChatId())) {
                    ConversationActivity.updateData();
                }
            }
        }
        if (!isFound) throw new RecordNotFoundException("Chat not found");
    }

    public Chat getChatById(Integer chatId) {
        boolean isFound = false;
        for (Chat chat : chats) {
            if (Objects.equals(chat.getId(), chatId)) {
                isFound = true;
                return chat;
            }
        }
        if (!isFound) throw new RecordNotFoundException("Chat not found");
        return null;
    }

    private void save() {
        String json = gson.toJson(chats, new TypeToken<ArrayList<Chat>>() {}.getType());
        MainActivity.preferenceManager.putString(Constants.KEY_CHATS, json);
    }

    private ArrayList<Chat> getChatsFromPrefs() {
        chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        return chats;
    }
}
