package su.ezhidze.enigma.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import su.ezhidze.enigma.adapters.ConversationAdapter;
import su.ezhidze.enigma.databinding.ActivityConversationBinding;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.InputOutputMessageModel;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.networks.WSService;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

public class ConversationActivity extends BaseActivity {

    private static ActivityConversationBinding binding;

    private static ConversationAdapter conversationAdapter;

    private PreferenceManager preferenceManager;

    private static Chat chat;

    private User receiverUser;

    private String conversationId;

    private final boolean isReceiverAvailable = false;

    private final String receiverPhoneNumber = null;

    public static ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadReceiverDetails();
        setListeners();
        init();
        listMessages();
        binding.conversationRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.layoutSend.setOnClickListener(view -> sendMessage());
    }

    private void init() {
        preferenceManager = MainActivity.preferenceManager;
        chatManager = MainActivity.chatManager;

        conversationAdapter = new ConversationAdapter(
                chat,
                getBitmapFromEncodedUrl(receiverUser.getImage()),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.conversationRecyclerView.setAdapter(conversationAdapter);
    }

    private void listMessages() {
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void loadReceiverDetails() {
        chat = (Chat) getIntent().getSerializableExtra(Constants.KEY_CHAT);
        for (User user : chat.getUsers()) {
            if (!Objects.equals(user.getId(), MainActivity.preferenceManager.getString(Constants.KEY_ID))) {
                receiverUser = user;
            }
        }
        binding.textUserName.setText(receiverUser.getNickname());
    }

    private Bitmap getBitmapFromEncodedUrl(String image) {
        if (image != null) {
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void sendMessage() {
        if (binding.inputMessage.getText().toString().trim().isEmpty()) {
            return;
        }
        InputOutputMessageModel message = new InputOutputMessageModel(preferenceManager.getString(Constants.KEY_NAME), chat.getId(), binding.inputMessage.getText().toString().trim());
        WSService.sendEchoViaStomp(message);
        chatManager.addMessage(message);
        binding.inputMessage.setText(null);
    }

    public static void updateData() {
        Integer chatId = chat.getId();
        chat = chatManager.getChatById(chatId);
        conversationAdapter.updateChatList(chat);
        binding.conversationRecyclerView.smoothScrollToPosition(chat.getMessages().size() - 1);
    }

    public static Chat getChat() {
        return chat;
    }
}