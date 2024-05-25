package su.ezhidze.enigma.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import su.ezhidze.enigma.R;
import su.ezhidze.enigma.databinding.ActivitySignInBinding;
import su.ezhidze.enigma.models.AuthenticationModel;
import su.ezhidze.enigma.models.AuthenticationResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignInBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setClickListener();
    }

    private void setClickListener() {
        binding.textCreateNewAccount.setOnClickListener(this);
        binding.buttonSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.textCreateNewAccount) {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.buttonSignIn) {
            if (isValidSignInDetails()) {
                try {
                    signIn();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            Toast.makeText(this, "Not valid Click", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidSignInDetails() {

        if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter Name");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }

    private void signIn() throws NoSuchAlgorithmException {
        loading(true);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            preferenceManager.putString(Constants.KEY_PRIVATE, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        }

        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
        Retrofit retrofit = ApiClient.getApiClient();
        ApiService apiService = retrofit.create(ApiService.class);
        preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
        preferenceManager.putString(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        AuthenticationModel authenticationModel =
                null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            authenticationModel = new AuthenticationModel(preferenceManager.getString(Constants.KEY_NAME), preferenceManager.getString(Constants.KEY_PASSWORD),
                    Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        }
        Call<AuthenticationResponseModel> authenticationModelCall = apiService.authentication(authenticationModel);
        authenticationModelCall.enqueue(new Callback<AuthenticationResponseModel>() {
            @Override
            public void onResponse(Call<AuthenticationResponseModel> call, Response<AuthenticationResponseModel> response) {
                loading(false);
                if (response.code() == 200) {
                    preferenceManager.putString(Constants.KEY_NAME, response.body().getNickname());
                    preferenceManager.putString(Constants.KEY_TOKEN, response.body().getToken());
                    preferenceManager.putString(Constants.KEY_ID, String.valueOf(response.body().getId()));
                    preferenceManager.putString(Constants.KEY_PHONE, response.body().getPhoneNumber());
                    preferenceManager.putString(Constants.KEY_IMAGE, response.body().getImage());
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    loading(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
            public void onFailure(Call<AuthenticationResponseModel> call, Throwable throwable) {
                loading(false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

}