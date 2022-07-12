package com.example.accord;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    public Button bCreate;
    public final String TAG = "CreateAccount";
    public EditText etUsername;
    public EditText etPassword;
    public EditText etName;
    public EditText etConfirmPassword;
    public EditText etDate;
    public EditText etBio;
    public ConstraintLayout clCreate;
    public ConstraintLayout clWait;
    public TextView tvCreateError;
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

    public void queryUsers(String UID, String password, String bday, String bio){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        JSONArray deckArr = new JSONArray();
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for(int i = 0; i < users.size(); i++){
                    deckArr.put(users.get(i).getUid());
                    try {
                        JSONArray currDeck = users.get(i).getMetadata().getJSONArray("Deck");
                        Log.e(TAG, currDeck.toString());
                        currDeck.put(UID); //Adding new user to the deck
                        users.get(i).getMetadata().put("Deck", currDeck);
                        Log.e(TAG, users.get(i).getMetadata().getJSONArray("Deck").toString());
                        CometChat.updateUser(users.get(i), authKey, new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.e(TAG, "User updated");
                            }

                            @Override
                            public void onError(CometChatException e) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "User list initialized");
                pushMetadata(deckArr, UID, password, bday, bio);
                //Log.e(TAG, "Metadata updated with deck: " + metadata);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Issue creating userlist : " + e.getMessage());
            }
        });
    }

    public void pushMetadata(JSONArray deckArr, String UID, String password, String bday, String bio){
        try {
            metadata.put("Deck", deckArr);
            metadata.put("PFP", "");
            metadata.put("Bio", bio);
            metadata.put("Right", 1);
            metadata.put("Left", 1);
            metadata.put("Birthday", bday);
            Log.e(TAG, "Bday: " + bday);
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
    }

    public void setup(){
        clCreate = findViewById(R.id.clCreate);
        clCreate.setVisibility(View.VISIBLE);
        clWait = findViewById(R.id.clWait);
        clWait.setVisibility(View.GONE);
        bCreate = findViewById(R.id.bCreateAcc);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDate = findViewById(R.id.etDate);
        etBio = findViewById(R.id.etBio);
        new DateInputMask(etDate);
        etName = findViewById(R.id.etName);
        tvCreateError = findViewById(R.id.tvCreateError);
        tvCreateError.setText("");
    }

    public void listeners(){
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Create button clicked");
                String username = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String bio = etBio.getText().toString().trim();
                int age = -1;
                try {
                    age = valiDate(date);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                if (username.isEmpty() || pass.isEmpty() || name.isEmpty() || date.isEmpty() || date.charAt(0) == 'M' || bio.isEmpty()) {
                    //Lt user know
                    Log.e(TAG, "Field missing");
                    tvCreateError.setText("Please fill out missing field(s)");
                    return;
                }
                else if(!pass.equals(confirmPass)){
                    Log.e(TAG, "Passwords do not match");
                    tvCreateError.setText("Passwords do not match");
                    return;
                }
                else if(bio.length() > 140){
                    Log.e(TAG, "Bio too long");
                    tvCreateError.setText("Bio is too long");
                    return;
                }
                else if(age < 0){
                    Log.e(TAG, "Invalid birthday");
                    tvCreateError.setText("Invalid birthday. Users must be between 12-100.");
                    return;
                }
                Log.e(TAG, "Success");
                clCreate.setVisibility(View.GONE);
                clWait.setVisibility(View.VISIBLE);
                createUser(name, username, pass, date, bio);
                //Log.e(TAG, "User created.");
            }
        });


    }

    public int valiDate(String date) throws java.text.ParseException { //must be in mm/dd/yyyy
        //Log.e(TAG, "Date: " + date);
        if(date.isEmpty()){
            return -1;
        }
        if(date.charAt(date.length()-1) == 'Y') {
            // Log.e(TAG, "Missing date");
            return -1;
        }
        SimpleDateFormat formatter =new SimpleDateFormat("MM/dd/yyyy");
        Date dDate = formatter.parse(date);
        Date currDate = Calendar.getInstance().getTime();
        // Log.e(TAG, "Date: " + dDate);
        // Log.e(TAG, "Current date: " + currDate);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        int years = Integer.parseInt(simpleDateformat.format(currDate))- Integer.parseInt(simpleDateformat.format(dDate));
        if(years < 12 || years > 100){
            // Log.e(TAG, "Please enter a valid date");
            return -1;
        }
        Log.e(TAG, "Age: " + years);
        return years;
    }

    public void createUser(String name, String username, String pass, String birthday, String bio){
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
                createChatUser(username, name, pass, birthday, bio);
            }
        });
    }

    public void createChatUser(String username, String name, String pass, String bday, String bio){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        com.cometchat.pro.models.User user = new com.cometchat.pro.models.User();
        user.setUid(username); // Replace with the UID for the user to be created
        user.setName(name); // Replace with the name of the user

        CometChat.createUser(user, authKey, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Chat User created" + user.toString());
                login(username, pass, bday, bio);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("createUser failed", e.getMessage());
            }
        });
    }

    public void login(String username, String password, String bday, String bio){
        loginChatUser(username, password, bday, bio);
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
                Intent i = new Intent(getApplicationContext(), CometChatUI.class);
                startActivity(i);
            }
        });
    }

    public void loginChatUser(String UID, String password, String bday, String bio){
        String authKey = BuildConfig.AUTH_KEY_CHAT;
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "Chat Login Worked: " + user.toString());
                    queryUsers(UID, password, bday, bio);
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