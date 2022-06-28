package com.cometchat.pro.uikit.ui_components;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.MediaAdapter;
import com.cometchat.pro.uikit.R;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MyCardFragment extends Fragment {

    public Koloda kCard;
    private CardSwipeAdapter mainAdapter;
    private MediaAdapter mediaAdapter;
    private List<String> list;
    public ImageView ivLogo;
    public final String TAG = "Card";
    public boolean vidVisible;
    public LinearLayout left;
    public LinearLayout right;
    public int top;
    public int page = 1;

    public MyCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_card, container, false);
        Log.e(TAG, "Logged in as: " + ParseUser.getCurrentUser().getUsername());
        Log.e(TAG, "Chat logged in as: " + CometChat.getLoggedInUser());
        setupMain(v);
        listeners();
        return v;
    }




    public void setupMain(View v){
        kCard = (Koloda) v.findViewById(R.id.kolCard);
        list = new ArrayList<String>();
        left = v.findViewById(R.id.leftLayout);
        right = v.findViewById(R.id.rightLayout);
        vidVisible = true;
        mainAdapter = new CardSwipeAdapter(getContext(), list, vidVisible);
        initCards();
        kCard.setAdapter(mainAdapter);
        flushFirst(kCard);
        ivLogo = v.findViewById(R.id.ivLogo);
    }

    public void initCards(){
        list.clear();
        try {
            JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
            list.add(""); //blank first card
            for(int i = 0; i < Deck.length(); i++){
                list.add(Deck.get(i).toString());
                Log.e(TAG, "Swipe user added:" + Deck.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainAdapter.notifyDataSetChanged();
        //kCard.reloadAdapterData();
    }

    public void flushFirst(Koloda kCard){
        kCard.reloadAdapterData();
    }

    public void listeners(){
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "Left on " + page);
                mainAdapter.setVidVisible(true);
                kCard.reloadAdapterData();
                /*if(page-1 == 1){
                    mainAdapter.setVidVisible(true);
                    kCard.reloadAdapterData();
                    page--;
                }
                else if(page != 1){
                    page--;
                }*/
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "Right on " + page);
                mainAdapter.setVidVisible(false);
                kCard.reloadAdapterData();
                if(page == 1){
                }
                page++;
            }
        });

        kCard.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
                top = i+1;
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                Log.e(TAG, "Left on: " + list.get(i+1));
                list.remove(i+1);
                mainAdapter.notifyDataSetChanged();
                kCard.reloadAdapterData();
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.e(TAG, "Right on: " + list.get(i+1));
                list.remove(i+1);
                mainAdapter.notifyDataSetChanged();
                kCard.reloadAdapterData();
            }

            @Override
            public void onClickRight(int i) {
            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {
                Log.e(TAG, "Tap sw");
                //mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCardDoubleTap(int i) {

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

            }
        });
    }

}