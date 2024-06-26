package su.ezhidze.enigma.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import su.ezhidze.enigma.R;
import su.ezhidze.enigma.adapters.MainActivityViewPagerFragmentsAdapter;
import su.ezhidze.enigma.databinding.ActivityMainBinding;
import su.ezhidze.enigma.fragments.ChatsFragment;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.ChatModel;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.networks.NetworksHelper;
import su.ezhidze.enigma.networks.WSService;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private MainActivityViewPagerFragmentsAdapter viewPagerFragmentsAdapter;

    public static PreferenceManager preferenceManager;

    public static ChatManager chatManager;

    private Retrofit retrofit;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Enigma");

        preferenceManager = new PreferenceManager(getApplicationContext());
        chatManager = new ChatManager(preferenceManager);
        retrofit = ApiClient.getApiClient();
        apiService = retrofit.create(ApiService.class);

        viewPagerFragmentsAdapter = new MainActivityViewPagerFragmentsAdapter(this);
        binding.viewPager.setAdapter(viewPagerFragmentsAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(Constants.TITLES[position]);
        }).attach();

        setToolbarMenu();

        if (NetworksHelper.isOnline(this) && savedInstanceState == null) {
            WSService.connectStomp();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Call<ArrayList<ChatModel>> chatCheckCall = apiService.getUserChats(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)), "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
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
                                ChatsFragment.updateData();
                            }
                            break;
                        }
                    }
                    if (!isFound) chatManager.deleteChat(chat.getId());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ChatModel>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void setToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_main_activity);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.signOut) {
                    if (NetworksHelper.isOnline(MainActivity.this)){
                        Call<Void> chatsRemovalCall = apiService.deleteUserChats(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)), "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
                        chatsRemovalCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d(TAG, "Chats removed");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, t.toString());
                            }
                        });

                        Call<UserResponseModel> signOutUserCall = apiService.signOutUser(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)), "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
                        signOutUserCall.enqueue(new Callback<UserResponseModel>() {
                            @Override
                            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                                Log.d(TAG, "Signed out!");
                            }

                            @Override
                            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                                Log.e(TAG, t.toString());
                            }
                        });
                        preferenceManager.clear();
                        WSService.disconnectStomp();
                        goToSignInActivity();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void goToSignInActivity() {
        finishAffinity();
        preferenceManager.clear();
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}