package su.ezhidze.enigma.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import su.ezhidze.enigma.activities.UsersActivity;
import su.ezhidze.enigma.models.Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatService {

    public ArrayList<Chat> chats;

    public PreferenceManager preferenceManager;

    private final Gson gson;

    public ChatService(PreferenceManager pM) {
        preferenceManager = pM;
        gson = new Gson();
        chats = new ArrayList<>();
        if (preferenceManager.getString(Constants.KEY_CHATS) != null) {
            chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), chats.getClass());
        } else preferenceManager.putString(Constants.KEY_CHATS, gson.toJson(chats));
    }

    public void save() {
        String json = gson.toJson(chats, new TypeToken<ArrayList<Chat>>() {}.getType());
        preferenceManager.putString(Constants.KEY_CHATS, json);
    }

    public ArrayList<Chat> getChatsFromPrefs() {
        chats = gson.fromJson(preferenceManager.getString(Constants.KEY_CHATS), new TypeToken<ArrayList<Chat>>() {}.getType());
        return chats;
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }
}
