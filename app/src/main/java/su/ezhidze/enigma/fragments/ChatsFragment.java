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
import su.ezhidze.enigma.listeners.RecentConversationUserListener;
import su.ezhidze.enigma.models.ChatMessage;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment implements RecentConversationUserListener {

    private FragmentChatsBinding binding;

    private List<ChatMessage> conversationUserList;

    private RecentConversationUsersAdapter conversationUsersAdapter;

    private PreferenceManager preferenceManager;

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
        init();
        setClickListeners();
    }

    private void init() {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        conversationUserList = new ArrayList<>();
        conversationUsersAdapter = new RecentConversationUsersAdapter(conversationUserList, this);
        binding.recentConversationUsersRecyclerView.setAdapter(conversationUsersAdapter);
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
    public void onUserClicked(User user) {
        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}