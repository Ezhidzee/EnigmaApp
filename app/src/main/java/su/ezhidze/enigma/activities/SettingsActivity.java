package su.ezhidze.enigma.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import su.ezhidze.enigma.databinding.ActivitySettingsBinding;
import su.ezhidze.enigma.models.ImageModel;
import su.ezhidze.enigma.models.UserResponseModel;
import su.ezhidze.enigma.networks.ApiClient;
import su.ezhidze.enigma.networks.ApiService;
import su.ezhidze.enigma.networks.NetworksHelper;
import su.ezhidze.enigma.utilities.BaseActivity;
import su.ezhidze.enigma.utilities.Constants;
import su.ezhidze.enigma.utilities.PreferenceManager;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    private String encodedImage;

    private PreferenceManager preferenceManager;

    private Retrofit retrofit;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofit = ApiClient.getApiClient();
        apiService = retrofit.create(ApiService.class);

        setUserData();

        setListeners();

    }

    private void setUserData() {
        loading(true);

        binding.imageUserProfile.setImageBitmap(getImageFromEncodedString(preferenceManager.getString(Constants.KEY_IMAGE)));
        binding.textUserName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textPhoneNumber.setText(preferenceManager.getString(Constants.KEY_PHONE));
        binding.collapsingToolbarLayout.setTitle(preferenceManager.getString(Constants.KEY_NAME));

        loading(false);
    }

    private Bitmap getImageFromEncodedString(String keyImage) {
        if (keyImage == null) return null;
        byte[] bytes = Base64.decode(keyImage, Base64.DEFAULT);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    private void setListeners() {
        binding.fabSelectUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworksHelper.isOnline(SettingsActivity.this)) {
                    pickImage.launch("image/*");
                }
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(result);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = encodeImage(bitmap);
                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                        ImageModel image = new ImageModel(encodedImage);
                        Call<UserResponseModel> imageSetCall = apiService.setImage(Integer.valueOf(preferenceManager.getString(Constants.KEY_ID)), image, "Bearer " + preferenceManager.getString(Constants.KEY_TOKEN));
                        imageSetCall.enqueue(new Callback<UserResponseModel>() {
                            @Override
                            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                                Log.d(TAG, "Image changed successfully!");
                                binding.imageUserProfile.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                                Log.e(TAG, t.toString());
                            }
                        });
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}