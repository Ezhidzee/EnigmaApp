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
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.ChatModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;
import su.ezhidze.enigma.networks.WSService;

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

        binding.toolbar.setTitle("Messenger");

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

        WSService.connectStomp();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Call<ArrayList<ChatModel>> chatCheckCall = apiService.getUserChats(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)));
        chatCheckCall.enqueue(new Callback<ArrayList<ChatModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatModel>> call, Response<ArrayList<ChatModel>> response) {

                for (Chat chat : chatManager.getChatList()) {
                    boolean isFound = false;
                    for (ChatModel ch : response.body()) {
                        if (chat.getId().equals(ch.getId())) {
                            isFound = true;
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
                    preferenceManager.putString(Constants.KEY_TOKEN, "");
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
                    goToSignInActivity();
                    Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                    return true;
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}