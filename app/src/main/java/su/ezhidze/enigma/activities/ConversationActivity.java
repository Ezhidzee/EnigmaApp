package su.ezhidze.enigma.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import su.ezhidze.enigma.adapters.ConversationAdapter;
import su.ezhidze.enigma.databinding.ActivityConversationBinding;
import su.ezhidze.enigma.models.ChatMessage;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationActivity extends BaseActivity {

    private ActivityConversationBinding binding;

    private ConversationAdapter conversationAdapter;

    private PreferenceManager preferenceManager;

    private List<ChatMessage> chatMessageList;

    private User receiverUser;

    private String conversationId;

    private final boolean isReceiverAvailable = false;

    private final String receiverPhoneNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadReceiverDetails();
        setListeners();
        init();
        listMessages();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessageList = new ArrayList<>();

        //Set adapter to  recyclerview
        conversationAdapter = new ConversationAdapter(
                chatMessageList,
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
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textUserName.setText(receiverUser.getName());
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

}