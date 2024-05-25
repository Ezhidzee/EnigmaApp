package su.ezhidze.enigma.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import su.ezhidze.enigma.R;
import su.ezhidze.enigma.activities.ConversationActivity;
import su.ezhidze.enigma.activities.MainActivity;
import su.ezhidze.enigma.activities.UsersActivity;
import su.ezhidze.enigma.adapters.RecentConversationUsersAdapter;
import su.ezhidze.enigma.databinding.FragmentChatsBinding;
import su.ezhidze.enigma.listeners.RecentConversationChatListener;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.ChatModel;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.networks.NetworksHelper;
import su.ezhidze.enigma.networks.WSService;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;


public class ChatsFragment extends Fragment implements RecentConversationChatListener {

    private FragmentChatsBinding binding;

    private static List<Chat> chatList;

    private static RecentConversationUsersAdapter conversationUsersAdapter;

    private PreferenceManager preferenceManager;

    private static ChatManager chatManager;

    private Retrofit retrofit;

    private ApiService apiService;

    public ChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = MainActivity.preferenceManager;
        chatManager = MainActivity.chatManager;
        chatList = (List<Chat>) chatManager.getChatList().clone();
        conversationUsersAdapter = new RecentConversationUsersAdapter(chatList, this);
        binding.recentConversationUsersRecyclerView.setAdapter(conversationUsersAdapter);
        retrofit = ApiClient.getApiClient();
        apiService = retrofit.create(ApiService.class);
        setClickListeners();
        binding.recentConversationUsersRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        setSwipeToDelete();
        binding.swiperefresh.setColorSchemeResources(R.color.primary);
    }

    private void setClickListeners() {
        binding.fabNewChat.setOnClickListener(view -> {
            if (NetworksHelper.isOnline(getContext())) {
                startActivity(new Intent(getContext(), UsersActivity.class));
            }
        });

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworksHelper.isOnline(getContext())) {
                    if (!WSService.isConnected()) WSService.connectStomp();
                    Call<ArrayList<ChatModel>> chatCheckCall = apiService.getUserChats(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)));
                    chatCheckCall.enqueue(new Callback<ArrayList<ChatModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<ChatModel>> call, Response<ArrayList<ChatModel>> response) {

                            for (Chat chat : chatManager.getChatList()) {
                                boolean isFound = false;
                                for (ChatModel ch : response.body()) {
                                    if (chat.getId().equals(ch.getId())) {
                                        isFound = true;
                                        User receiverUser = null;
                                        UserResponseModel receiverUserModel = null;
                                        for (User u : chat.getUsers()) {
                                            if (!u.getId().equals(preferenceManager.getString(Constants.KEY_ID))) {
                                                receiverUser = u;
                                            }
                                        }
                                        for (UserResponseModel u : ch.getUsers()) {
                                            if (!String.valueOf(u.getId()).equals(preferenceManager.getString(Constants.KEY_ID))) {
                                                receiverUserModel = u;
                                            }
                                        }
                                        if (!receiverUserModel.getImage().equals(receiverUser.getImage())) {
                                            receiverUser.setImage(receiverUserModel.getImage());
                                            chatManager.save();
                                        }
                                        break;
                                    }
                                }
                                if (!isFound) chatManager.deleteChat(chat.getId());
                            }
                            updateData();
                            binding.swiperefresh.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<ArrayList<ChatModel>> call, Throwable t) {
                            Log.e(TAG, t.toString());
                        }
                    });
                } else {
                    binding.swiperefresh.setRefreshing(false);
                }

            }
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
        chatList = (List<Chat>) chatManager.getChatList().clone();
        conversationUsersAdapter.updateChatList(chatList);
    }

    private void setSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Chat chat = chatList.get(viewHolder.getBindingAdapterPosition());

                int position = viewHolder.getBindingAdapterPosition();
                int chatId = chatList.get(position).getId();
                chatList.remove(position);
                conversationUsersAdapter.updateChatList(chatList);

                Snackbar.make(binding.recentConversationUsersRecyclerView, "Chat deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatList.add(position, chat);
                        conversationUsersAdapter.updateChatList(chatList);
                    }
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            super.onDismissed(transientBottomBar, event);
                            chatManager.deleteChat(chatId);
                            Call<Void> chatRemovalCall = apiService.deleteChat(chatId);
                            chatRemovalCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Log.d(TAG, "Chat removed");
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e(TAG, t.toString());
                                }
                            });

                        }
                    }
                }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(binding.recentConversationUsersRecyclerView);
    }

    private void setDeleteIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#b80f0a");
        Drawable deleteDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.baseline_delete_24);
        int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        int intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), mClearPaint);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);
    }
}