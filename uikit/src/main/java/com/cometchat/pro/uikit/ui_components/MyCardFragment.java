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

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.MediaAdapter;
import com.cometchat.pro.uikit.MyMedia;
import com.cometchat.pro.uikit.ProfPic;
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
    //private List<String> pfpList;
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
    public Button bAlg;

    public MyCardFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_card, container, false);
        v.clearAnimation();
        Log.e(TAG, "Logged in as: " + ParseUser.getCurrentUser().getUsername());
        Log.e(TAG, "Chat logged in as: " + CometChat.getLoggedInUser());
        try {
            initScoreList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            initUserList(); //also inits scorelist
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupMain(v);
        listeners();
        return v;
    }

    private void initScoreList() throws JSONException {
        scoreList = new ArrayList<Integer>();
        JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        for(int i = 0; i < Deck.length(); i++){
            scoreList.add(0);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initUserList() throws JSONException {
        userList = new ArrayList<User>();
        //scoreList = new ArrayList<Integer>();
        JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        for(int i = 0; i < Deck.length()+1; i++){
            if(i< Deck.length()){
                CometChat.getUser(Deck.get(i).toString(), new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        addToList(user); //add scores here
                        int score = scoreAlg(user);
                        int index = -1;
                        for(int i = 0; i < userList.size(); i++){
                            if(userList.get(i) == user){
                                index = i;
                            }
                        }
                        scoreList.set(index, score);
                        Log.e(TAG, "Added score " + score + " to user " + user.getUid());
                        //queryPFP(user.getUid());
                    }
                    @Override
                    public void onError(CometChatException e) {

                    }
                });
            }
            else{
                //initCardsFiltered("");
            }
        }
    }

    public int scoreAlg(User user){
        return user.getUid().length();
    }

    /*private void queryPFP(String UID) { //returns a URL
        Log.e(TAG, "Setting pfp");
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        query.include(ProfPic.KEY_USER);
        query.whereContains(ProfPic.KEY_USER, UID);
        // start an asynchronous call for pfp
        query.findInBackground(new FindCallback<ProfPic>() {
            @Override
            public void done(List<ProfPic> pics, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting pfp", e);
                    return;
                }
                if(pics.size() == 0) {
                    //set pfp to default
                    addToPFPList("");
                }
                else{
                    //set pfp
                    Log.e(TAG, "Setting existing PFP");
                    addToPFPList(pics.get(0).getImage().getUrl());
                }
            }

        });
    }*/

    public void addToList(User user){
        userList.add(user);
        //scoreList.add(1);
    }


    /*public void addToPFPList(String url){
        pfpList.add(url);
    }*/

    public int getPositionOfBestMatch(List<Integer> tempScoreList){
        int highestScore = Collections.max(tempScoreList);
        int indx = tempScoreList.indexOf(highestScore);
        Log.e(TAG, "Score list: " + tempScoreList + ", indx highest: " + indx);
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
        bAlg = v.findViewById(R.id.bApplyAlg);
        vidVisible = true;
        mainAdapter = new CardSwipeAdapter(getContext(), list, vidVisible, "");
        //mainAdapter.setPfps(pfpList);
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
            Log.e("CHECK", "Scorelist size: " + scoreList.size());
            for(int i = 0; i< scoreList.size(); i++){
                tempScoreList.add(scoreList.get(i));
            }
            Log.e("CHECK", "Temp list: " + tempScoreList);
            for(int i = 0; i < Deck.length(); i++){
                int maxIndx = getPositionOfBestMatch(tempScoreList);
                Log.e(TAG, "Best match is at: " + maxIndx);
                list.add(Deck.get(maxIndx).toString());
                //Log.e(TAG, "Swipe user added:" + Deck.get(maxIndx).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMediaCount();
        Log.e(TAG, "List being passed in: " + list);
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

    public void removeMeFromDeck(String UID){
        CometChat.getUser(UID, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                try {
                    // Log.e(TAG, "Removing from: " + user.getName());
                    JSONArray userDeck = user.getMetadata().getJSONArray("Deck");
                    // Log.e(TAG, "Deck: " + userDeck);
                    String me = CometChat.getLoggedInUser().getUid();
                    // Log.e(TAG, "Me: " + me);
                    int pos = -1;
                    for(int i = 0; i<userDeck.length(); i++){
                        if(userDeck.get(i).equals(me)){
                            pos = i;
                        }
                    }
                    // Log.e(TAG, "I'm at " + pos);
                    if(pos != -1){
                        user.getMetadata().getJSONArray("Deck").remove(pos);
                        CometChat.updateUser(user, "85e114ed71f14e3ce779b2673d876b9faa8bc5ff", new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.e(TAG, "Removed myself from deck of " + user.getName());
                            }

                            @Override
                            public void onError(CometChatException e) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Error removing: " + e.getMessage());
            }
        });
    }

    public void addToFavorites(String UID) throws JSONException {
        CometChat.getLoggedInUser().getMetadata().getJSONArray("Liked").put(UID);
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "Added to favorites: " + UID);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    public void removeFromMyDeck(String UID) throws JSONException {
        JSONArray myDeck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        int pos = -1;
        for(int i = 0; i<myDeck.length(); i++){
            if(myDeck.get(i).equals(UID)){
                pos = i;
            }
        }
        // Log.e(TAG, "I'm at " + pos);
        if(pos != -1) {
            CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck").remove(pos);
            CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.e(TAG, "Removed from my deck: " + UID);
                }

                @Override
                public void onError(CometChatException e) {

                }
            });
        }
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
                    mainAdapter.notifyDataSetChanged();
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

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Scores: " + scoreList);
                Log.e(TAG, "Users: " + userList);
                Log.e(TAG, "User list: " + list);
                //initCardsFiltered("");
                //kCard.reloadAdapterData();
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

        bAlg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                initCardsFiltered("");
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
                /*try {
                    removeMeFromDeck(list.get(1)); //passes the UID of the person I swiped left on
                    removeFromMyDeck(list.get(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/ //uncomment when I want to actually update the decks
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
                    /*try {
                        removeFromMyDeck(list.get(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    sendIntroMessage(list.get(1));
                    matchDialog();
                }
                if(!isLikedBy(list.get(1))){
                    Log.e(TAG, "No match");
                    /*try {
                        addToFavorites(list.get(1));
                        removeFromMyDeck(list.get(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
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

    public void matchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.match_alert, (ViewGroup) getView(), false);
        final Button bOk = (Button) viewInflated.findViewById(R.id.bOkMatch);
        alert.setView(viewInflated);

        alert.show();

        bOk.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Ok clicked");
                alert.cancel();
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