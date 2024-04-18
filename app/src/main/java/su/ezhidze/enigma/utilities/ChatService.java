package su.ezhidze.enigma.utilities;

import com.google.gson.Gson;
import su.ezhidze.enigma.models.Chat;

import java.util.ArrayList;

public class ChatService {

    public ArrayList<Chat> chats;

    public PreferenceManager preferenceManager;

    private final Gson gson = new Gson();

    public void save() {
        String json = gson.toJson(chats);
        preferenceManager.putString(Constants.KEY_CHATS, json);
    }

    public ArrayList<Chat> getChatsFromPrefs() {
        chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), chats.getClass());
        return chats;
    }
}
