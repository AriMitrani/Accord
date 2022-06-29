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
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.MediaAdapter;
import com.cometchat.pro.uikit.MyMedia;
import com.cometchat.pro.uikit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
        v.clearAnimation();
        Log.e(TAG, "Logged in as: " + ParseUser.getCurrentUser().getUsername());
        Log.e(TAG, "Chat logged in as: " + CometChat.getLoggedInUser());
        setupMain(v);
        listeners();
        return v;
    }




    public void setupMain(View v){
        kCard = (Koloda) v.findViewById(R.id.kolCard);
        //kCard.setAnimation(null);
        kCard.setLayoutAnimation(null);
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
                // Log.e(TAG, "Swipe user added:" + Deck.get(i).toString());
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
                if(page-1 == 1){
                    mainAdapter.setVidVisible(true);
                    kCard.reloadAdapterData();
                    page--;
                }
                else if(page != 1){
                    page--;
                }
                Log.e(TAG, "Current page: " + page);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mediaCount = 0;
                try {
                    mediaCount = mainAdapter.queryMedia(list.get(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.e(TAG,list.get(1) + " has " + mediaCount + " media items/pages");
                //Log.e(TAG, "Right on " + page);
                if(page != 1 && page <= mediaCount) {
                    kCard.reloadAdapterData();
                    page++;
                }
                else if(page == 1 && page <= mediaCount) {
                    mainAdapter.setVidVisible(false);
                    kCard.reloadAdapterData();
                    page++;
                }
                Log.e(TAG, "Current page: " + page +" " + list.get(1));
            }
        });

        kCard.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
                //top = i+1;
                Log.e(TAG, "Top page: " + list.get(1));
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                Log.e(TAG, "Left on: " + list.get(1));
                list.remove(0);
                page = 1;
                mainAdapter.setVidVisible(true);
                mainAdapter.notifyDataSetChanged();
                kCard.reloadAdapterData();
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.e(TAG, "Right on: " + list.get(1));
                list.remove(0);
                page = 1;
                mainAdapter.notifyDataSetChanged();
                mainAdapter.setVidVisible(true);
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