package su.ezhidze.enigma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import su.ezhidze.enigma.adapters.UsersAdapter;
import su.ezhidze.enigma.databinding.ActivityUsersBinding;
import su.ezhidze.enigma.listeners.UserListener;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;

    private PreferenceManager preferenceManager;

//    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        Retrofit retrofit = ApiClient.getApiClient();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<ArrayList<UserResponseModel>> usersListCall = apiService.getUsers();
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

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}