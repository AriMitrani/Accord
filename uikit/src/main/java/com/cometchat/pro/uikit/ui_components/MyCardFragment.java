package com.cometchat.pro.uikit.ui_components;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.VideoView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.MediaAdapter;
import com.cometchat.pro.uikit.MyMedia;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_components.users.user_list.CometChatUserList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyCardFragment extends Fragment {

    public Koloda kCard;
    private CardSwipeAdapter mainAdapter;
    private List<String> list;
    private List<User> userList;
    private List<Integer> scoreList;
    public ImageView ivLogo;
    public final String TAG = "Card";
    public boolean vidVisible;
    public LinearLayout left;
    public LinearLayout right;
    public int top;
    public int page = 1;
    public String filter;
    public Button bFilter;

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
        try {
            initUserList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initScoreList();
        setupMain(v);
        listeners();
        return v;
    }

    private void initScoreList() {
        scoreList = new ArrayList<Integer>();
        scoreList.add(1); //jomas
        scoreList.add(11); //bil
        scoreList.add(10); //tay
        scoreList.add(4); //joel
    }

    public void initUserList() throws JSONException {
        userList = new ArrayList<User>();
        JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        for(int i = 0; i < Deck.length(); i++){
            CometChat.getUser(Deck.get(i).toString(), new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    addToList(user);
                }

                @Override
                public void onError(CometChatException e) {

                }
            });
        }
    }

    public void addToList(User user){
        userList.add(user);
    }

    public int getPositionOfBestMatch(List<Integer> tempScoreList){
        int highestScore = Collections.max(tempScoreList);
        int indx = tempScoreList.indexOf(highestScore);
        tempScoreList.set(indx, -1);
        return indx;
    }


    public void setupMain(View v){
        kCard = (Koloda) v.findViewById(R.id.kolCard);
        filter = "";
        //kCard.setAnimation(null);
        kCard.setLayoutAnimation(null);
        list = new ArrayList<String>();
        left = v.findViewById(R.id.leftLayout);
        right = v.findViewById(R.id.rightLayout);
        bFilter = v.findViewById(R.id.bFilter);
        vidVisible = true;
        mainAdapter = new CardSwipeAdapter(getContext(), list, vidVisible, "");
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
            List<Integer> tempScoreList = new ArrayList<Integer>();
            for(int i = 0; i< scoreList.size(); i++){
                tempScoreList.add(scoreList.get(i));
            }
            for(int i = 0; i < Deck.length(); i++){
                int maxIndx = getPositionOfBestMatch(tempScoreList);
                list.add(Deck.get(maxIndx).toString());
                // Log.e(TAG, "Swipe user added:" + Deck.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMediaCount();
        mainAdapter.notifyDataSetChanged();
        //kCard.reloadAdapterData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initCardsFiltered(String filt){
        list.clear();
        list.add(""); //blank first card
        List<Integer> tempScoreList = new ArrayList<Integer>();
        for(int i = 0; i< scoreList.size(); i++){
            tempScoreList.add(scoreList.get(i));
        }
        for(int i = 0; i < userList.size(); i++){
            int maxIndx = getPositionOfBestMatch(tempScoreList);
            User user = userList.get(maxIndx);
            if(user.getMetadata().toString().contains(filt)){
                list.add(user.getUid());
            }
        }
        Log.e(TAG, "Filtered list " + list);
        Log.e(TAG, "Score list " + scoreList);
        mainAdapter.setPage(1);
        mainAdapter.setVidVisible(true);
        mainAdapter.notifyDataSetChanged();
        updateMediaCount();
        kCard.reloadAdapterData();
    }


    public void flushFirst(Koloda kCard){
        kCard.reloadAdapterData();
    }

    public void updateMediaCount(){
        try {
            if(list.size()>1){
                int mediaCount = mainAdapter.queryMedia(list.get(1));
                mainAdapter.setVisiblePages(mediaCount);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isLikedBy(String username) {
        User user = new User();
        for(int i = 0; i< userList.size(); i++){
            if(userList.get(i).getUid().equals(username)){
                user = userList.get(i);
            }
        }
        try {
            if(user.getMetadata().getJSONArray("Liked").toString().contains(CometChat.getLoggedInUser().getUid())){
                return true;
            }
            else{
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void listeners(){
        left.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "Left on " + page);
                if(page-1 == 1){
                    mainAdapter.setVidVisible(true);
                    page--;
                    mainAdapter.setPage(page);
                    kCard.reloadAdapterData();
                }
                else if(page != 1){
                    page--;
                    mainAdapter.setPage(page);
                    kCard.reloadAdapterData();
                }
                Log.e(TAG, "Current page: " + page);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    page++;
                    mainAdapter.setPage(page);
                    kCard.reloadAdapterData();
                }
                else if(page == 1 && page <= mediaCount) {
                    mainAdapter.setVidVisible(false);
                    page++;
                    mainAdapter.setPage(page);
                    kCard.reloadAdapterData();
                }
                Log.e(TAG, "Current page: " + page +" " + list.get(1));
            }
        });

        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(filter.equals("drums")){
                    filter = "";
                    Log.e(TAG, "Filter off");
                }
                else{
                    filter = "drums";
                    Log.e(TAG, "Filter drums");
                }*/
                /*if(mainAdapter.doesPassFilter() == true){
                    Log.e(TAG,"Pass " + mainAdapter.getNamePass());
                }
                else{
                    Log.e(TAG,"No" + mainAdapter.getNamePass());
                }*/
                filterDialog();
                /*for(int i = 0; i< userList.size(); i++){
                    Log.e(TAG, "User " + userList.get(i));
                }
                initCardsFiltered("drum");*/
            }
        });

        kCard.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
                //top = i+1;
                // Log.e(TAG, "Top page: " + list.get(1));
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCardSwipedLeft(int i) {
                Log.e(TAG, "Left on: " + list.get(1));
                list.remove(0);
                updateMediaCount();
                page = 1;
                mainAdapter.setVidVisible(true);
                mainAdapter.setPage(1);
                mainAdapter.notifyDataSetChanged();
                kCard.reloadAdapterData();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCardSwipedRight(int i) {
                Log.e(TAG, "Right on: " + list.get(1));
                if(isLikedBy(list.get(1))){
                    Log.e(TAG, "Match!");
                    sendIntroMessage(list.get(1));
                }
                if(!isLikedBy(list.get(1))){
                    Log.e(TAG, "No match");
                }
                updateMediaCount();
                list.remove(0);
                updateMediaCount();
                page = 1;
                mainAdapter.setVidVisible(true);
                mainAdapter.setPage(1);
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
                mainAdapter.test(page);
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

    public void sendIntroMessage(String username) {
        User user = new User();
        for(int i = 0; i< userList.size(); i++){
            if(userList.get(i).getUid().equals(username)){
                user = userList.get(i);
            }
        }
        TextMessage textMessage = new TextMessage(user.getUid(), "Hey, we just matched!", CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                //go to fragment!
            }

            @Override
            public void onError(CometChatException e) {
                // cry rlly hard
            }
        });

    }

    public void filterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.filter_alert, (ViewGroup) getView(), false);
        final Button bApply = (Button) viewInflated.findViewById(R.id.bApplyFilters);
        final RadioButton rbGuitar = viewInflated.findViewById(R.id.rbGuitar);
        final RadioButton rbVocals = viewInflated.findViewById(R.id.rbVocals);
        final RadioButton rbDrum = viewInflated.findViewById(R.id.rdDrum);
        alert.setView(viewInflated);

        alert.show();

        rbGuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter = "guitar";
            }
        });

        rbDrum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter = "drum";
            }
        });

        rbVocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter = "vocal";
            }
        });

        bApply.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Apply clicked");
                initCardsFiltered(filter);
                alert.cancel();
            }
        });

    }


}