package su.ezhidze.enigma.networks;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import su.ezhidze.enigma.activities.ConversationActivity;
import su.ezhidze.enigma.activities.MainActivity;
import su.ezhidze.enigma.exceptions.RecordNotFoundException;
import su.ezhidze.enigma.models.Chat;
import su.ezhidze.enigma.models.ChatModel;
import su.ezhidze.enigma.models.InputOutputMessageModel;
import su.ezhidze.enigma.models.Message;
import su.ezhidze.enigma.models.User;
import su.ezhidze.enigma.utilities.ChatManager;
import su.ezhidze.enigma.utilities.Constants;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WSService {

    private static StompClient mStompClient;

    private static CompositeDisposable compositeDisposable;

    private static Gson gson;

    private static ChatManager chatManager;

    private static Retrofit retrofit;

    private static ApiService apiService;

    static {
        gson = new Gson();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/chat/websocket");
        chatManager = MainActivity.chatManager;
        retrofit = ApiClient.getApiClient();
        apiService = retrofit.create(ApiService.class);
    }

    public static void connectStomp() {
        resetSubscriptions();

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + MainActivity.preferenceManager.getString(Constants.KEY_TOKEN)));

        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);

        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG,"Stomp connection opened");
                            sendConnectionNotification();
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.i(TAG,"Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.i(TAG,"Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        Disposable dispTopic = mStompClient.topic("/user/topic/private-messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    InputOutputMessageModel inputMessage = gson.fromJson(topicMessage.getPayload(), InputOutputMessageModel.class);
                    Log.v(TAG, "Received " + inputMessage.getMessageText());

                    try {
                        chatManager.addMessage(inputMessage);
                    } catch (RecordNotFoundException e) {
                        Chat newChat = new Chat();
                        Call<ChatModel> chatModelCall = apiService.getChatById(inputMessage.getChatId());
                        chatModelCall.enqueue(new Callback<ChatModel>() {
                            @Override
                            public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                                newChat.setId(response.body().getId());
                                newChat.setUsers(response.body().getUsers().stream().map(User::new).collect(Collectors.toList()));
                                newChat.setMessages(response.body().getMessages().stream().map(Message::new).collect(Collectors.toList()));
                                chatManager.addChat(newChat);
                                chatManager.addMessage(inputMessage);
                            }

                            @Override
                            public void onFailure(Call<ChatModel> call, Throwable t) {
                                Log.e(TAG, "Error on new chat creation", t);
                            }
                        });
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        Disposable notifTopic = mStompClient.topic("/user/topic/private-notifications")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    List<InputOutputMessageModel> unreadMessages = gson.fromJson(topicMessage.getPayload(), new TypeToken<List<InputOutputMessageModel>>() {}.getType());
                    Log.v(TAG, String.valueOf(unreadMessages.size()));
                    for (InputOutputMessageModel message : unreadMessages) {
                        try {
                            chatManager.addMessage(message);
                        } catch (RecordNotFoundException e) {
                            Chat newChat = new Chat();
                            Call<ChatModel> chatModelCall = apiService.getChatById(message.getChatId());
                            chatModelCall.enqueue(new Callback<ChatModel>() {
                                @Override
                                public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                                    newChat.setId(response.body().getId());
                                    newChat.setUsers(response.body().getUsers().stream().map(User::new).collect(Collectors.toList()));
                                    newChat.setMessages(response.body().getMessages().stream().map(Message::new).collect(Collectors.toList()));
                                    chatManager.addChat(newChat);
                                    chatManager.addMessage(message);
                                }

                                @Override
                                public void onFailure(Call<ChatModel> call, Throwable t) {
                                    Log.e(TAG, "Error on new chat creation", t);
                                }
                            });
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);

        mStompClient.connect(headers);
    }

    public static void sendEchoViaStomp(final InputOutputMessageModel message) {
        String outputMessage = gson.toJson(message, InputOutputMessageModel.class);
        compositeDisposable.add(mStompClient.send("/app/private-chat", outputMessage)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                }));
    }

    private static void sendConnectionNotification() {
        compositeDisposable.add(mStompClient.send("/app/connection-notifications", MainActivity.preferenceManager.getString(Constants.KEY_NAME) + " connected!")
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                }));
    }

    private static void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private static CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
