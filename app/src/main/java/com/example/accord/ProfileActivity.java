package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    //MENU ITEMS
    public Button bMenuProfile;
    public Button bMenuCards;
    public Button bMenuChat;
    //END MENU ITEMS

    public TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setup();
        menuSetup();
        listeners();
    }

    public void setup(){
        //tvTitle = findViewById(R.id.tvLabel);
        //tvTitle.setPaintFlags(tvTitle.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
    }

    public void menuSetup(){
        bMenuProfile = findViewById(R.id.bMenuProfile);
        bMenuCards = findViewById(R.id.bMenuCard);
        bMenuChat = findViewById(R.id.bMenuChat);

        bMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Already here
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
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
            }
        });
    }

    public void listeners(){}
}