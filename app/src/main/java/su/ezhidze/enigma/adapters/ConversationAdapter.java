package su.ezhidze.enigma.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import su.ezhidze.enigma.activities.MainActivity;
import su.ezhidze.enigma.databinding.ItemContainerReceivedMessageBinding;
import su.ezhidze.enigma.databinding.ItemContainerSentMessageBinding;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.Message;
import su.ezhidze.enigma.utilities.Constants;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Chat chat;

    private Bitmap receiverProfileImage;

    public static final int VIEW_TYPE_SENT = 1;

    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap) {
        receiverProfileImage = bitmap;
    }

    @Override
    public int getItemViewType(int position) {
        if (chat.getMessages().get(position).getSenderSubject().equals(MainActivity.preferenceManager.getString(Constants.KEY_NAME))) {
            return VIEW_TYPE_SENT;
        } else return VIEW_TYPE_RECEIVED;
    }

    public ConversationAdapter(Chat chat, Bitmap receiverProfileImage) {
        this.chat = chat;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_SENT == viewType) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        } else {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chat.getMessages().get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chat.getMessages().get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chat.getMessages().size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(Message message) {
            binding.textSentMessage.setText(message.getMessageText());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(Message message, Bitmap receiverProfile) {
            binding.textReceivedMessage.setText(message.getMessageText());
            if (receiverProfile != null) {
                binding.imageSenderProfile.setImageBitmap(receiverProfile);
            }
        }
    }

    public void updateChatList(Chat newChat) {
        this.chat = newChat;
        this.notifyDataSetChanged();
    }
}
