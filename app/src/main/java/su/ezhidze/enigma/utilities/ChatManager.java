package su.ezhidze.enigma.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import su.ezhidze.enigma.models.Chat;

import java.util.ArrayList;

public class ChatManager {

    private ArrayList<Chat> chats;

    private PreferenceManager preferenceManager;

    private final Gson gson;

    public ChatManager(PreferenceManager pM) {
        preferenceManager = pM;
        gson = new Gson();
        chats = new ArrayList<>();
        if (preferenceManager.getString(Constants.KEY_CHATS) != null) {
            chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), chats.getClass());
        } else preferenceManager.putString(Constants.KEY_CHATS, gson.toJson(chats));
    }

    public ArrayList<Chat> getChats() {
        return getChatsFromPrefs();
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        save();
    }

    private void save() {
        String json = gson.toJson(chats, new TypeToken<ArrayList<Chat>>() {}.getType());
        preferenceManager.putString(Constants.KEY_CHATS, json);
    }

    private ArrayList<Chat> getChatsFromPrefs() {
        chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        return chats;
    }
}
