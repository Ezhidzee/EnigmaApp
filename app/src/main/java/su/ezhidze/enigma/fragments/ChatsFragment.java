package su.ezhidze.enigma.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import su.ezhidze.enigma.activities.ConversationActivity;
import su.ezhidze.enigma.activities.UsersActivity;
import su.ezhidze.enigma.adapters.RecentConversationUsersAdapter;
import su.ezhidze.enigma.databinding.FragmentChatsBinding;
import su.ezhidze.enigma.listeners.RecentConversationChatListener;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.util.List;


public class ChatsFragment extends Fragment implements RecentConversationChatListener {

    private FragmentChatsBinding binding;

    private static List<Chat> chatList;

    private static RecentConversationUsersAdapter conversationUsersAdapter;

    private PreferenceManager preferenceManager;

    private static ChatManager chatManager;

    public ChatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        chatManager = new ChatManager(preferenceManager);
        chatList = chatManager.getChats();
        conversationUsersAdapter = new RecentConversationUsersAdapter(chatList, this);
        binding.recentConversationUsersRecyclerView.setAdapter(conversationUsersAdapter);
        setClickListeners();
        binding.recentConversationUsersRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void setClickListeners() {
        binding.fabNewChat.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), UsersActivity.class));
        });
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onChatClicked(Chat chat) {
        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtra(Constants.KEY_CHAT, chat);
        startActivity(intent);
    }

    public static void updateData() {
        chatList = chatManager.getChats();
        conversationUsersAdapter.updateChatList(chatList);
    }
}