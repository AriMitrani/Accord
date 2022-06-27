package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public Button bLogin;
    public final String TAG = "Login";
    public TextView tvNew;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e(TAG, "Login launched");
        setup();
        listeners();

        // createChatUser(ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getString("fullName"));

        if (ParseUser.getCurrentUser() != null) { //if someone is already logged in, skip login screen
            loginChatUser(ParseUser.getCurrentUser().getUsername());
            goMainActivity();
        }

    }

    public void setup(){
        bLogin = findViewById(R.id.bLogin);
        tvNew = findViewById(R.id.tvNew);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    public void listeners(){
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(etUsername.getText().toString(), etPassword.getText().toString()); //hardcoded login, eventually change to login/create acc
            }
        });

        tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
            }
        });
    }

    public void goMainActivity(){
        Intent i = new Intent(this, CometChatUI.class);
        startActivity(i);
        finish();
    }

    public void login(String username, String password){
        loginParseUser(username, password);
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
                Log.i(TAG, "Login worked");
                loginChatUser(username);
            }
        });
    }

    public void loginChatUser(String UID){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {

                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "Login Successful : " + user.toString());
                    goMainActivity(); //function we define to go from login screen to main if login success
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Login failed with exception: " + e.getMessage());
                }
            });
        } else {
            Log.d(TAG, "User already logged in: " + UID);
            // goMainActivity();
        }
    }

}