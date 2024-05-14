package su.ezhidze.enigma.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import su.ezhidze.enigma.activities.MainActivity;
import su.ezhidze.enigma.databinding.ItemContainerRecentConversationUserBinding;
import su.ezhidze.enigma.listeners.RecentConversationChatListener;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.utilities.Constants;

import java.util.List;
import java.util.Objects;

public class RecentConversationUsersAdapter extends RecyclerView.Adapter<RecentConversationUsersAdapter.RecentConversationViewHolder> {

    private List<Chat> chatList;

    private final RecentConversationChatListener recentConversationChatListener;

    public RecentConversationUsersAdapter(List<Chat> chatList, RecentConversationChatListener recentConversationChatListener) {
        this.chatList = chatList;
        this.recentConversationChatListener = recentConversationChatListener;
    }

    @NonNull
    @Override
    public RecentConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentConversationViewHolder(ItemContainerRecentConversationUserBinding.inflate(LayoutInflater.from(
                parent.getContext()
        ), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationViewHolder holder, int position) {
            holder.setData(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class RecentConversationViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRecentConversationUserBinding binding;

        RecentConversationViewHolder(ItemContainerRecentConversationUserBinding itemContainerRecentConversationUserBinding) {
            super(itemContainerRecentConversationUserBinding.getRoot());
            binding = itemContainerRecentConversationUserBinding;
        }

        void setData(Chat chat) {
            User user = new User();
            for (User i : chat.getUsers()) {
                if (!Objects.equals(i.getId(), MainActivity.preferenceManager.getString(Constants.KEY_ID))) {
                    user = i;
                }
            }
            binding.imageProfile.setImageBitmap(getConversationBitmap(user.getImage()));
            binding.textName.setText(user.getNickname());
            if (!chat.getMessages().isEmpty()) {
                binding.textRecent.setText(chat.getMessages().get(chat.getMessages().size() - 1).getMessageText());
            } else binding.textRecent.setText("");
            binding.getRoot().setOnClickListener(v -> {
                recentConversationChatListener.onChatClicked(chat);
            });
        }
    }

    private Bitmap getConversationBitmap(String encodedString) {
        if(encodedString == null) return null;
        byte[] bytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void updateChatList(List<Chat> chatList) {
        this.chatList = chatList;
        this.notifyDataSetChanged();
    }
}
