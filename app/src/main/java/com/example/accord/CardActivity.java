package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {
    //MENU ITEMS
    public Button bMenuProfile;
    public Button bMenuCards;
    public Button bMenuChat;
    //END MENU ITEMS

    public Koloda kCard;
    private SwipeAdapter adapter;
    private List<Integer> list;
    public ImageView ivLogo;
    public final String TAG = "Card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        setup();
        menuSetup();
        listeners();
        Log.e(TAG, "Logged in as: " + ParseUser.getCurrentUser().getUsername());
        Log.e(TAG, "Chat logged in as: " + CometChat.getLoggedInUser());
    }

    public void setup(){
        kCard = findViewById(R.id.kolCard);
        list = new ArrayList<Integer>();
        initCards();
        adapter = new SwipeAdapter(this, list);
        kCard.setAdapter(adapter);
        ivLogo = findViewById(R.id. ivLogo);
    }

    public void initCards(){
        list.add(1);
        list.add(2);
        list.add(3);
    }


    public void menuSetup(){
        bMenuProfile = findViewById(R.id.bMenuProfile);
        bMenuCards = findViewById(R.id.bMenuCard);
        bMenuChat = findViewById(R.id.bMenuChat);

        bMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        bMenuCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Already here
            }
        });

        bMenuChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
            }
        });
    }

    public void listeners(){
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout(){
        ParseUser.logOutInBackground();
        logoutComet();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    public void logoutComet(){
        if (CometChat.getLoggedInUser() != null) {
            CometChat.logout(new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.e(TAG, "Logout success");
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "Logout error: " + e);
                }
            });
        }
    }

}