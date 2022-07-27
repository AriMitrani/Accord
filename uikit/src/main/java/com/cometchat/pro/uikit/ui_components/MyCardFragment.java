package com.cometchat.pro.uikit.ui_components;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CardSwipeAdapter;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyCardFragment extends Fragment {

    public Koloda kCard;
    private CardSwipeAdapter mainAdapter;
    private List<String> list;
    private List<String> passedList;
    private List<User> userList;
    private List<Double> scoreList;
    public ImageView ivLogo;
    public final String TAG = "CardFrag";
    public boolean vidVisible;
    public LinearLayout left;
    public LinearLayout right;
    public int top;
    public int page = 1;
    public String filterI;
    public int filterL;
    public Button bFilter;
    public Button bAlg;
    public TextView tvBacking;

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
            initUserList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupMain(v);
        listeners();
        return v;
    }

    private void initScoreList() throws JSONException {
        scoreList = new ArrayList<Double>();
        JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        for (int i = 0; i < Deck.length(); i++) {
            scoreList.add(0.0);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initUserList() throws JSONException {
        userList = new ArrayList<User>();
        JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        for (int i = 0; i < Deck.length() + 1; i++) {
            if (i < Deck.length()) {
                CometChat.getUser(Deck.get(i).toString(), new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        addToList(user);
                        double score = 0;
                        try {
                            score = scoreAlg(user, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        int index = -1;
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i) == user) {
                                index = i;
                            }
                        }
                        scoreList.set(index, score);
                        Log.e(TAG, "Added score " + score + " to user " + user.getUid());
                    }

                    @Override
                    public void onError(CometChatException e) {

                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public double scoreAlg(User user, int distanceConstraint) throws JSONException, java.text.ParseException { //main algorithm
        User me = CometChat.getLoggedInUser();
        double ageScore = 0;
        double locationScore = 0;
        double instrumentScore = 0;
        double ELO = 0;
        double genreScore = 0;

        int myAge = getAge(me.getMetadata().get("Birthday").toString());
        int theirAge = getAge(user.getMetadata().get("Birthday").toString());
        ageScore = setAgeScore(myAge, theirAge);

        locationScore = setLocationScore(me, user, distanceConstraint);

        instrumentScore = setInstrumentScore(me.getMetadata().getJSONArray("Skills"), user.getMetadata().getJSONArray("Skills"));

        ELO = setELO(user);

        genreScore = setGenreScore(me.getMetadata().getJSONArray("Genres"), user.getMetadata().getJSONArray("Genres"));

        double finalScore = (0.15 * ageScore + 0.25 * locationScore + 0.3 * instrumentScore + 0.1 * ELO + 0.2 * genreScore);
        return finalScore;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge(String date) throws java.text.ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date dDate = formatter.parse(date);
        Date currDate = Calendar.getInstance().getTime();

        return Period.between(convertToLocalDateViaInstant(dDate), convertToLocalDateViaInstant(currDate)).getYears();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public double setAgeScore(int ageMe, int ageThem) {
        int diff = ageMe - ageThem;
        if (diff < 0) {
            diff *= -1;
        }
        if (ageMe <= 17) {
            if (diff <= 1) {
                return 1;
            }
            if (ageThem >= 18 || diff >= 3) {
                return 0;
            } else {
                return 0.5;
            }
        } else if (ageMe > 17 && ageMe <= 25) {
            if (ageThem <= 16) {
                return 0;
            } else if (diff <= 1) {
                return 1;
            } else if (diff <= 3) {
                return 0.5;
            } else {
                return 0;
            }
        } else {
            if (ageThem <= 23) {
                return 0;
            } else if (diff <= 5) {
                return 1;
            } else if (diff <= 10) {
                return 0.5;
            } else {
                return 0.3;
            }
        }
    }

    public double setLocationScore(User me, User you, int distanceConstraint) throws JSONException {
        double distance = getDistance(me, you);
        double diff = distance - distanceConstraint;
        if (distance <= distanceConstraint) {
            return 1;
        } else if (diff >= 60) {
            return 0;
        } else {
            return (1 - diff / 60);
        }
    }

    public double setInstrumentScore(JSONArray mySkills, JSONArray theirSkills) throws JSONException {
        if (mySkills.length() == 1 && theirSkills.length() == 1 && mySkills.get(0).equals(theirSkills.get(0))) { //if they play the same, single instrument
            return 0.3;
        } else {
            double myAvg = 0;
            double theirAvg = 0;
            for (int i = 0; i < mySkills.length(); i++) {
                String skill = mySkills.get(i).toString();
                double level = Integer.parseInt(skill.substring(skill.length() - 1));
                myAvg += level;
            }
            myAvg /= mySkills.length();

            for (int i = 0; i < theirSkills.length(); i++) {
                String skill = theirSkills.get(i).toString();
                double level = Integer.parseInt(skill.substring(skill.length() - 1));
                theirAvg += level;
            }
            theirAvg /= theirSkills.length();

            if (theirAvg > myAvg) {
                return myAvg / theirAvg;
            } else {
                return theirAvg / myAvg;
            }
        }
    }

    public double getDistance(User me, User you) throws JSONException { //in miles
        Location startPoint = new Location("me");
        startPoint.setLatitude(me.getMetadata().getDouble("Lat"));
        startPoint.setLongitude(me.getMetadata().getDouble("Lon"));

        Location endPoint = new Location("you");
        endPoint.setLatitude(you.getMetadata().getDouble("Lat"));
        endPoint.setLongitude(you.getMetadata().getDouble("Lon"));

        double distance = startPoint.distanceTo(endPoint) * 0.000621371;
        return distance;
    }

    public double setELO(User user) throws JSONException {
        return user.getMetadata().getDouble("Right") / (user.getMetadata().getDouble("Left") * 10);
    }

    public double setGenreScore(JSONArray myGenres, JSONArray theirGenres) throws JSONException {
        if (myGenres.equals(theirGenres)) {
            return 1;
        }

        JSONArray allGenres = new JSONArray();
        int sharedGenres = 0;
        for (int i = 0; i < myGenres.length(); i++) {
            allGenres.put(myGenres.getString(i));
        }
        for (int i = 0; i < theirGenres.length(); i++) {
            if (!isInArray(theirGenres.getString(i), allGenres)) {
                allGenres.put(theirGenres.getString(i));
            } else {
                sharedGenres++;
            }
        }

        if (allGenres.length() == myGenres.length() || allGenres.length() == theirGenres.length()) { //one array completely encompasses the other
            return 0.8;
        }

        return sharedGenres / allGenres.length();
    }

    public boolean isInArray(String string, JSONArray array) throws JSONException {
        boolean inArr = false;
        for (int i = 0; i < array.length(); i++) {
            if (array.getString(i).equals(string)) {
                inArr = true;
            }
        }
        return inArr;
    }

    public void addToList(User user) {
        userList.add(user);
    }

    public int getPositionOfBestMatch(List<Double> tempScoreList) {
        double highestScore = Collections.max(tempScoreList);
        int indx = tempScoreList.indexOf(highestScore);
        tempScoreList.set(indx, -1.0);
        return indx;
    }


    public void setupMain(View v) {
        kCard = (Koloda) v.findViewById(R.id.kolCard);
        filterI = "";
        filterL = 500;
        kCard.setLayoutAnimation(null);
        list = new ArrayList<String>();
        passedList = new ArrayList<String>();
        left = v.findViewById(R.id.leftLayout);
        right = v.findViewById(R.id.rightLayout);
        bFilter = v.findViewById(R.id.bFilter);
        tvBacking = v.findViewById(R.id.tvBacking);
        moreCards();
        bAlg = v.findViewById(R.id.bApplyAlg);
        vidVisible = true;
        mainAdapter = new CardSwipeAdapter(getContext(), passedList, vidVisible, "");
        initCards();
        kCard.setAdapter(mainAdapter);
        flushFirst(kCard);
        ivLogo = v.findViewById(R.id.ivLogo);
    }

    public void initCards() {
        list.clear();
        try {
            JSONArray Deck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
            list.add("");
            List<Double> tempScoreList = new ArrayList<Double>();
            for (int i = 0; i < scoreList.size(); i++) {
                tempScoreList.add(scoreList.get(i));
            }
            for (int i = 0; i < Deck.length(); i++) {
                int maxIndx = getPositionOfBestMatch(tempScoreList);
                list.add(Deck.get(maxIndx).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMediaCount();
        passedList.clear();
        passedList.add("");
        if (list.size() > 1) {
            passedList.add(list.get(1));
        } else {
            cardsRunOut();
        }
        mainAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initCardsFiltered(String filtI, int filtL) throws JSONException {
        list.clear();
        list.add("");
        List<Double> tempScoreList = new ArrayList<Double>();
        for (int i = 0; i < scoreList.size(); i++) {
            tempScoreList.add(scoreList.get(i));
        }
        for (int i = 0; i < userList.size(); i++) {
            int maxIndx = getPositionOfBestMatch(tempScoreList);
            User user = userList.get(maxIndx);
            if (user.getMetadata().toString().contains(filtI) && getDistance(CometChat.getLoggedInUser(), user) <= filtL) {
                list.add(user.getUid());
            }
        }
        passedList.clear();
        passedList.add("");
        if (list.size() > 1) {
            passedList.add(list.get(1));
            mainAdapter.setPage(1);
            mainAdapter.setVidVisible(true);
            mainAdapter.notifyDataSetChanged();
            updateMediaCount();
        } else {
            cardsRunOut();
        }
        kCard.reloadAdapterData();
    }


    public void flushFirst(Koloda kCard) {
        kCard.reloadAdapterData();
    }

    public void updateMediaCount() {
        try {
            if (list.size() > 1) {
                int mediaCount = mainAdapter.queryMedia(list.get(1));
                mainAdapter.setVisiblePages(mediaCount);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isLikedBy(String username) {
        User user = new User();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUid().equals(username)) {
                user = userList.get(i);
            }
        }
        try {
            if (user.getMetadata().getJSONArray("Liked").toString().contains(CometChat.getLoggedInUser().getUid())) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeMeFromDeck(String UID) {
        CometChat.getUser(UID, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                try {
                    JSONArray userDeck = user.getMetadata().getJSONArray("Deck");
                    String me = CometChat.getLoggedInUser().getUid();
                    int pos = -1;
                    for (int i = 0; i < userDeck.length(); i++) {
                        if (userDeck.get(i).equals(me)) {
                            pos = i;
                        }
                    }
                    if (pos != -1) {
                        user.getMetadata().getJSONArray("Deck").remove(pos);
                        CometChat.updateUser(user, "85e114ed71f14e3ce779b2673d876b9faa8bc5ff", new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                            }

                            @Override
                            public void onError(CometChatException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateELO(String UID, String dir) {
        CometChat.getUser(UID, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                try {
                    if (dir.equals("Left")) {
                        int left;
                        left = (int) user.getMetadata().get("Left");
                        user.getMetadata().put("Left", left + 1);
                    } else {
                        int right;
                        right = (int) user.getMetadata().get("Right");
                        user.getMetadata().put("Right", right + 1);
                    }
                    CometChat.updateUser(user, "85e114ed71f14e3ce779b2673d876b9faa8bc5ff", new CometChat.CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                        }

                        @Override
                        public void onError(CometChatException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        });
    }

    public void removeFromMyDeck(String UID) throws JSONException {
        JSONArray myDeck = CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck");
        int pos = -1;
        for (int i = 0; i < myDeck.length(); i++) {
            if (myDeck.get(i).equals(UID)) {
                pos = i;
            }
        }
        if (pos != -1) {
            CometChat.getLoggedInUser().getMetadata().getJSONArray("Deck").remove(pos);
            CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {

                }

                @Override
                public void onError(CometChatException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void listeners() {
        left.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (list.size() > 1) {
                    if (page - 1 == 1) {
                        mainAdapter.setVidVisible(true);
                        page--;
                        mainAdapter.setPage(page);
                        kCard.reloadAdapterData();
                    } else if (page != 1) {
                        page--;
                        mainAdapter.setPage(page);
                        mainAdapter.notifyDataSetChanged();
                        kCard.reloadAdapterData();
                    }
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (list.size() > 1) {
                    int mediaCount = 0;
                    try {
                        mediaCount = mainAdapter.queryMedia(list.get(1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (page != 1 && page <= mediaCount) {
                        page++;
                        mainAdapter.setPage(page);
                        kCard.reloadAdapterData();
                    } else if (page == 1 && page <= mediaCount) {
                        mainAdapter.setVidVisible(false);
                        page++;
                        mainAdapter.setPage(page);
                        kCard.reloadAdapterData();
                    }
                }
            }
        });

        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });

        bAlg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    initCardsFiltered("", 500);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        kCard.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCardSwipedLeft(int i) {
                updateELO(list.get(1), "Left");
                try {
                    removeMeFromDeck(list.get(1));
                    removeFromMyDeck(list.get(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.remove(0);
                updateMediaCount();
                page = 1;
                mainAdapter.setVidVisible(true);
                mainAdapter.setPage(1);
                updatePassedList();
                kCard.reloadAdapterData();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCardSwipedRight(int i) {
                updateELO(list.get(1), "Right");
                if (isLikedBy(list.get(1))) {
                    try {
                        removeFromMyDeck(list.get(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendIntroMessage(list.get(1));
                    matchDialog(list.get(1));
                }
                if (!isLikedBy(list.get(1))) {
                    try {
                        addToFavorites(list.get(1));
                        removeFromMyDeck(list.get(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateMediaCount();
                list.remove(0);
                updateMediaCount();
                page = 1;
                mainAdapter.setVidVisible(true);
                mainAdapter.setPage(1);
                updatePassedList();
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

    public void updatePassedList() {
        passedList.clear();
        passedList.add(list.get(0));
        if (list.size() > 1) {
            passedList.add(list.get(1));
            moreCards();
        } else {
            cardsRunOut();
        }
        mainAdapter.notifyDataSetChanged();
    }

    public void moreCards() {
        tvBacking.setText("Keep swiping! Music is around the corner...");
    }

    public void cardsRunOut() {
        tvBacking.setText("Out of cards! Try removing some filters to see more potential bandmates.");
    }

    public void sendIntroMessage(String username) {
        User user = new User();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUid().equals(username)) {
                user = userList.get(i);
            }
        }
        TextMessage textMessage = new TextMessage(user.getUid(), "Hey, we just matched!", CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
            }
        });

    }

    public void matchDialog(String matchID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.match_alert, (ViewGroup) getView(), false);
        final Button bQuitAlert = (Button) viewInflated.findViewById(R.id.bOkMatch);
        final Button bGoToChat = (Button) viewInflated.findViewById(R.id.bGoChat);
        alert.setView(viewInflated);

        alert.show();

        bQuitAlert.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Ok clicked");
                alert.cancel();
            }
        });

        bGoToChat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Chat button clicked");
                CometChat.getUser(matchID, new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        startUserIntent(user);
                    }

                    @Override
                    public void onError(CometChatException e) {
                        e.printStackTrace();
                    }
                });
                alert.cancel();
            }
        });

    }

    private void startUserIntent(User user) {
        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName());
        intent.putExtra(UIKitConstants.IntentStrings.LINK, user.getLink());
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }

    public void filterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.filter_alert, (ViewGroup) getView(), false);
        final Button bApply = (Button) viewInflated.findViewById(R.id.bApplyFilters);
        final Button bClear = (Button) viewInflated.findViewById(R.id.bClearFilters);
        final RadioButton rbGuitar = viewInflated.findViewById(R.id.rbGuitar);
        final RadioButton rbVocals = viewInflated.findViewById(R.id.rbVocals);
        final RadioButton rbDrum = viewInflated.findViewById(R.id.rdDrum);
        final RadioButton rbBass = viewInflated.findViewById(R.id.rbBass);
        final RadioButton rbKeys = viewInflated.findViewById(R.id.rbKeys);
        final RadioButton rbProd = viewInflated.findViewById(R.id.rbProd);
        final RadioButton rd5 = viewInflated.findViewById(R.id.rd5);
        final RadioButton rd10 = viewInflated.findViewById(R.id.rd10);
        final RadioButton rd20 = viewInflated.findViewById(R.id.rd20);
        final RadioButton rd30 = viewInflated.findViewById(R.id.rd30);
        final RadioButton rd50 = viewInflated.findViewById(R.id.rd50);
        alert.setView(viewInflated);

        alert.show();

        rbGuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "guitar";
            }
        });

        rbDrum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "drum";
            }
        });

        rbVocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "vocal";
            }
        });

        rbBass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "bass";
            }
        });

        rbKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "keys";
            }
        });

        rbProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterI = "prod";
            }
        });


        rd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterL = 5;
            }
        });

        rd10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterL = 10;
            }
        });

        rd20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterL = 20;
            }
        });

        rd30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterL = 30;
            }
        });

        rd50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterL = 500;
            }
        });

        bApply.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Apply clicked");
                try {
                    initCardsFiltered(filterI, filterL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alert.cancel();
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clear clicked");
                try {
                    initCardsFiltered("", 500);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alert.cancel();
            }
        });

    }


}