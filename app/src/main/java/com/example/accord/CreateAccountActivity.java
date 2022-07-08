package com.example.accord;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    public Button bCreate;
    public final String TAG = "CreateAccount";
    public EditText etUsername;
    public EditText etPassword;
    public EditText etName;
    JSONObject metadata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initMetadata();
        setup();
        listeners();
        //COCKATOO add user to all decks
    }

    public void initMetadata(){
        String mString = Parcels.unwrap(getIntent().getParcelableExtra("metadata"));
        try {
            metadata = new JSONObject(mString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, String.valueOf(metadata));
    }

    public void queryUsers(String UID, String password){
        JSONArray deckArr = new JSONArray();
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for(int i = 0; i < users.size(); i++){
                    deckArr.put(users.get(i).getUid());
                }
                Log.e(TAG, "User list initialized");
                try {
                    metadata.put("Deck", deckArr);
                    metadata.put("Bio", "Test bio");
                    metadata.put("Birthday", "01012000");
                    CometChat.getLoggedInUser().setMetadata(metadata);
                    CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            Log.e(TAG, "Metadata upload worked: " + CometChat.getLoggedInUser().getMetadata());
                            Log.e(TAG, "Full user: " + CometChat.getLoggedInUser());
                            loginParseUser(UID, password);
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.e(TAG, "Updating metadata failed");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Metadata updated with deck: " + metadata);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Issue creating userlist : " + e.getMessage());
            }
        });
    }

    public void setup(){
        bCreate = findViewById(R.id.bCreateAcc);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
    }

    public void listeners(){
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Create button clicked");
                String username = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                if (username.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                    //Lt user know
                    Log.e(TAG, "Field missing");
                    return;
                }
                createUser(name, username, pass);
                //Log.e(TAG, "User created.");
            }
        });
    }

    public void createUser(String name, String username, String pass){
        // Log.e(TAG, "Creating account");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(pass);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error creating user: " + e);
                }
                if (String.valueOf(e).equals("com.parse.ParseRequest$ParseRequestException: Account already exists for this username")) {
                    Log.e(TAG, "Username taken");
                    //Display to user
                } else if (String.valueOf(e).equals("com.parse.ParseRequest$ParseRequestException: Account already exists for this email address.")) {
                    Log.e(TAG, "Email taken");
                    //Display to user
                }
                Log.e(TAG, "Parse User created!");
                createChatUser(username, name, pass);
            }
        });
    }

    public void createChatUser(String username, String name, String pass){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        com.cometchat.pro.models.User user = new com.cometchat.pro.models.User();
        user.setUid(username); // Replace with the UID for the user to be created
        user.setName(name); // Replace with the name of the user

        CometChat.createUser(user, authKey, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Chat User created" + user.toString());
                login(username, pass);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("createUser failed", e.getMessage());
            }
        });
    }

    public void login(String username, String password){
        loginChatUser(username, password);
    }

    public void loginParseUser(String username, String password) {
        Log.i(TAG, "Logging in " + username);

        //this statement logs the user into parse
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) { //e is error code, if it is null the parse login worked
                    Log.i(TAG, "Issue with login", e);
                    return;
                }
                Log.i(TAG, "Parse Login worked");
                Log.e(TAG, "Metadata at the end: " + CometChat.getLoggedInUser().getMetadata());
                //Intent i = new Intent(getApplicationContext(), CometChatUI.class);
                //startActivity(i);
            }
        });
    }

    public void loginChatUser(String UID, String password){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "Chat Login Worked: " + user.toString());
                    queryUsers(UID, password);
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Login failed with exception: " + e.getMessage());
                }
            });
        } else {
            Log.d(TAG, "User already logged in: " + UID);
        }
    }
}