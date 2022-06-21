package com.cometchat.pro.uikit.ui_components;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.R;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;

import java.util.ArrayList;
import java.util.List;

public class MyCardFragment extends Fragment {

    public Koloda kCard;
    private CardSwipeAdapter adapter;
    private List<Integer> list;
    public ImageView ivLogo;
    public final String TAG = "Card";

    public MyCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_card, container, false);
        Log.e(TAG, "Logged in as: " + ParseUser.getCurrentUser().getUsername());
        Log.e(TAG, "Chat logged in as: " + CometChat.getLoggedInUser());
        setup(v);
        return v;
    }

    public void setup(View v){
        kCard = (Koloda) v.findViewById(R.id.kolCard);
        list = new ArrayList<Integer>();
        initCards();
        adapter = new CardSwipeAdapter(getContext(), list);
        kCard.setAdapter(adapter);
        ivLogo = v.findViewById(R.id. ivLogo);
    }

    public void initCards(){
        list.add(1);
        list.add(2);
        list.add(3);
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