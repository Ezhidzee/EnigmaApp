package su.ezhidze.enigma.networks;

import su.ezhidze.enigma.models.AuthenticationModel;
import su.ezhidze.enigma.models.UserRegistrationModel;
import su.ezhidze.enigma.models.UserResponseModel;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("enigma/registration")
    Call<UserResponseModel> registration(
            @Body UserRegistrationModel userRegistrationModel
    );

    @POST("enigma/authentication")
    Call<Map<String, Object>> authentication(
            @Body AuthenticationModel authenticationModel
    );

    @GET("enigma")
    Call<ArrayList<UserResponseModel>> getUsers(
    );

}
