package su.ezhidze.enigma.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import su.ezhidze.enigma.R;
import su.ezhidze.enigma.databinding.ActivitySignUpBinding;
import su.ezhidze.enigma.models.AuthenticationModel;
import su.ezhidze.enigma.models.AuthenticationResponseModel;
import su.ezhidze.enigma.models.UserRegistrationModel;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignUpBinding binding;

    private String encodedImage;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        binding.pickerCountryCode.registerCarrierNumberEditText(binding.inputMobileNumber);

        setClickListeners();
    }

    private void setClickListeners() {
        binding.textSignIn.setOnClickListener(this);
        binding.buttonSignUp.setOnClickListener(this);
        binding.layoutUserImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.textSignIn) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.buttonSignUp) {
            if (isValidSignUpDetails()) {
                KeyPairGenerator generator = null;
                try {
                    generator = KeyPairGenerator.getInstance("RSA");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();
                PrivateKey privateKey = pair.getPrivate();
                PublicKey publicKey = pair.getPublic();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    preferenceManager.putString(Constants.KEY_PRIVATE, java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded()));
                }

                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_UP, false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
                Retrofit retrofit = ApiClient.getApiClient();
                ApiService apiService = retrofit.create(ApiService.class);
                UserRegistrationModel userRegistrationModel = new UserRegistrationModel(binding.inputName.getText().toString(),
                        binding.pickerCountryCode.getFullNumberWithPlus().replace(" ", ""),
                        binding.inputPassword.getText().toString());
                preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                preferenceManager.putString(Constants.KEY_PHONE, binding.pickerCountryCode.getFullNumberWithPlus().replace(" ", ""));
                preferenceManager.putString(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
                Call<UserResponseModel> userRegistrationModelCall = apiService.registration(userRegistrationModel);
                loading(true);
                userRegistrationModelCall.enqueue(new Callback<UserResponseModel>() {
                    @Override
                    public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                        loading(false);
                        if (response.code() == 200) {
                            preferenceManager.putString(Constants.KEY_ID, String.valueOf(response.body().getId()));
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_UP, true);
                            AuthenticationModel authenticationModel =
                                    null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                authenticationModel = new AuthenticationModel(preferenceManager.getString(Constants.KEY_NAME), preferenceManager.getString(Constants.KEY_PASSWORD),
                                        java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                            }
                            Call<AuthenticationResponseModel> authenticationModelCall = apiService.authentication(authenticationModel);

                            authenticationModelCall.enqueue(new Callback<AuthenticationResponseModel>() {
                                @Override
                                public void onResponse(Call<AuthenticationResponseModel> call, Response<AuthenticationResponseModel> response) {
                                    loading(false);
                                    if (response.code() == 200) {
                                        preferenceManager.putString(Constants.KEY_TOKEN, response.body().getToken());
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
                    public void onFailure(Call<UserResponseModel> call, Throwable throwable) {
                        loading(false);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_UP, false);
                    }
                });
            }
        } else if (id == R.id.layoutUserImage) {
            pickImage.launch("image/*");
        } else {
            Toast.makeText(this, "Invalid Click", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignUpDetails() {

        if (encodedImage == null) {
            showToast("Select Profile Image.");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter Name");
            return false;
        } else if (binding.inputMobileNumber.getText().toString().trim().isEmpty()) {
            showToast("Enter Mobile Number");
            return false;
        } else if (!binding.pickerCountryCode.isValidFullNumber()) {
            showToast("Enter a Valid Phone Number");
            return false;
        } else if (!Patterns.PHONE.matcher(binding.inputMobileNumber.getText().toString()).matches()) {
            showToast("Enter Valid Phone Number");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm Your Password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password & Confirm Password must be same");
            return false;
        } else {
            return true;
        }
    }

    ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(result);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.imageProfile.setImageBitmap(bitmap);
                encodedImage = encodeImage(bitmap);
                binding.textAddImage.setVisibility(View.INVISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    });

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}