package su.ezhidze.enigma.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import su.ezhidze.enigma.adapters.UsersAdapter;
import su.ezhidze.enigma.databinding.ActivityUsersBinding;
import su.ezhidze.enigma.listeners.UserListener;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;


public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;

    private PreferenceManager preferenceManager;

    private ChatManager chatManager;

    private Chat conversation;

    private Retrofit retrofit;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = MainActivity.preferenceManager;
        chatManager = MainActivity.chatManager;
        conversation = new Chat();
        retrofit = ApiClient.getApiClient();
        apiService = retrofit.create(ApiService.class);

        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUsers() {
        loading(true);

        Call<ArrayList<UserResponseModel>> usersListCall = apiService.getUsers("Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
        usersListCall.enqueue(new Callback<ArrayList<UserResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponseModel>> call, Response<ArrayList<UserResponseModel>> response) {
                if (response.code() == 200) {
                    loading(false);
                    ArrayList<UserResponseModel> userResponseModels = response.body();
                    if (userResponseModels.size() > 1) {
                        ArrayList<User> users = new ArrayList<>();
                        for (UserResponseModel i : userResponseModels) {
                            if (!Objects.equals(i.getNickname(), preferenceManager.getString(Constants.KEY_NAME))) {
                                users.add(new User(i));
                            }
                        }
                        UsersAdapter adapter = new UsersAdapter(users, UsersActivity.this);
                        binding.userRecyclerView.setAdapter(adapter);
                        binding.userRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        showToast(response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserResponseModel>> call, Throwable throwable) {
                loading(false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
            }
        });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No User Available."));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserClicked(User user) {
        boolean chatExists = false;
        for (Chat chat : chatManager.getChatList()) {
            for (User i : chat.getUsers()) {
                if (i.getId().equals(user.getId())) {
                    chatExists = true;
                    conversation = chat;
                }
            }
            if (chatExists) break;
        }
        if (!chatExists) {
            Call<Chat> chatCreationCall = apiService.addChat("Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
            chatCreationCall.enqueue(new Callback<Chat>() {
                @Override
                public void onResponse(Call<Chat> call, Response<Chat> response) {
                    conversation.setId(response.body().getId());

                    Call<Chat> addUser1Call = apiService.joinUser(conversation.getId(), Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)), "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
                    addUser1Call.enqueue(new Callback<Chat>() {
                        @Override
                        public void onResponse(Call<Chat> call, Response<Chat> response) {

                            Call<Chat> addUser2Call = apiService.joinUser(conversation.getId(), Integer.valueOf(user.getId()), "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
                            addUser2Call.enqueue(new Callback<Chat>() {
                                @Override
                                public void onResponse(Call<Chat> call, Response<Chat> response) {
                                    conversation.setUsers(response.body().getUsers());
                                    chatManager.addChat(conversation);
                                    Intent intent = new Intent(getContext(), ConversationActivity.class);
                                    intent.putExtra(Constants.KEY_CHAT, conversation);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<Chat> call, Throwable throwable) {
                                    showToast(throwable.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Chat> call, Throwable throwable) {
                            showToast(throwable.getMessage());
                        }
                    });
                }

                @Override
                public void onFailure(Call<Chat> call, Throwable throwable) {
                    showToast(throwable.getMessage());
                }
            });
        }
    }

    public Context getContext() {
        return getApplicationContext();
    }
}