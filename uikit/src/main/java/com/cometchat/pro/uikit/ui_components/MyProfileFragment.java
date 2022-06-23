package com.cometchat.pro.uikit.ui_components;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.R;
import com.parse.ParseUser;

public class MyProfileFragment extends Fragment {

    public final String TAG = "MyProfile";
    ImageView ivSettings;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        setup(v);
        //listeners();
        return v;
    }

    public void setup(View v){
        ivSettings = v.findViewById(R.id.ivSettings);
    }

    public void listeners(){
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    public void logout(){
        ParseUser.logOutInBackground();
        logoutComet();
        //Intent i = new Intent(getActivity().getIntent(), LoginActivity.class);
        //startActivity(i);
        getActivity().finish();
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