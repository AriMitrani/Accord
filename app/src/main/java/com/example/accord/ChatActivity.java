package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;

public class ChatActivity extends AppCompatActivity {
    //MENU ITEMS
    public Button bMenuProfile;
    public Button bMenuCards;
    public Button bMenuChat;
    //END MENU ITEMS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setup();
        menuSetup();
        listeners();
        startActivity(new Intent(ChatActivity.this, CometChatUI.class));
    }

    public void setup(){}

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
                Intent i = new Intent(getApplicationContext(), CardActivity.class);
                startActivity(i);
            }
        });

        bMenuChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Already here
            }
        });
    }

    public void listeners(){}
}