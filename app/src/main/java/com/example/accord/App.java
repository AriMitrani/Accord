package com.example.accord;

import android.app.Application;
import android.util.Log;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.MyMedia;
import com.cometchat.pro.uikit.ProfPic;
import com.parse.Parse;
import com.parse.ParseObject;

public class App extends Application {

    public final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        initChat();
        ParseObject.registerSubclass(ProfPic.class);
        initParse();
    }

    public void initChat() {
        String region = BuildConfig.REGION;
        String appID = BuildConfig.APP_ID_CHAT;
        AppSettings appSettings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .setRegion(region)
                .autoEstablishSocketConnection(true)
                .build();

        CometChat.init(this, appID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "Initialization completed successfully");
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
        ParseObject.registerSubclass(MyMedia.class);
    }

    public void initParse() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.B4A_APP_ID)
                .clientKey(BuildConfig.B4A_CLIENT_KEY)
                .server(BuildConfig.B4A_SERVER_URL)
                .build());
    }

}