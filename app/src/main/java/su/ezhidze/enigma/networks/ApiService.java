package su.ezhidze.enigma.networks;

import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import su.ezhidze.enigma.models.AuthenticationModel;
import su.ezhidze.enigma.models.AuthenticationResponseModel;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.ChatModel;
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
    Call<AuthenticationResponseModel> authentication(
            @Body AuthenticationModel authenticationModel
    );

    @GET("enigma")
    Call<ArrayList<UserResponseModel>> getUsers(
    );

    @POST("/addChat")
    Call<Chat> addChat(
    );

    @PUT("/joinUser")
    Call<Chat> joinUser(@Query("chatId") Integer chatId, @Query("userId") Integer userId);

    @GET("/chats")
    Call<ChatModel> getChatById(@Query("id") Integer id);

    @DELETE("/deleteChat")
    Call<Void> deleteChat(@Query("id") Integer id);

    @DELETE("/deleteUserChats")
    Call<Void> deleteUserChats(@Query("id") Integer id);

    @GET("enigma/getUserChats")
    Call<ArrayList<ChatModel>> getUserChats(@Query("userId") Integer userId);

    @POST("enigma/signOutUser")
    Call<UserResponseModel> signOutUser(@Query("id") Integer id);

    @PATCH("enigma/setImage")
    Call<UserResponseModel> setImage(@Query("id") Integer id, @Query("image") String image);
}
