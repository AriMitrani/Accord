package com.example.accord;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity {

    public Button bCreate;
    public final String TAG = "CreateAccount";
    public EditText etUsername;
    public EditText etPassword;
    public EditText etName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setup();
        listeners();

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
                Log.e(TAG, "User created.");
                //Intent i = new Intent(getApplicationContext(), CardActivity.class);
                //startActivity(i);
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
                // Log.e(TAG, "User created!");
                createChatUser(username, name);
            }
        });
    }

    public void createChatUser(String username, String name){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        com.cometchat.pro.models.User user = new com.cometchat.pro.models.User();
        user.setUid(username); // Replace with the UID for the user to be created
        user.setName(name); // Replace with the name of the user

        CometChat.createUser(user, authKey, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("createUser success", user.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("createUser failed", e.getMessage());
            }
        });
    }
}