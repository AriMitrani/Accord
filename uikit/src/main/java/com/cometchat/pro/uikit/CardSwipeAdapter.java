package com.cometchat.pro.uikit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardSwipeAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public final String TAG = "CardSwipeAdapter";
    public TextView tvCardName;
    public ImageView ivCardPic;
    public ImageView ivInst1;
    public ImageView ivInst2;
    public ImageView ivInst3;
    public RatingBar rb1;
    public RatingBar rb2;
    public RatingBar rb3;
    public ImageView iv1;
    public ImageView iv2;
    public ImageView iv3;
    public ImageView iv4;
    public ImageView iv5;
    public ImageView iv6;
    public ImageView iv7;
    public TextView tvLocation;
    public TextView tvAge;
    public int LayoutID;
    public int page;
    public VideoView vFile;
    public boolean vidVisible;
    public List<MyMedia> mediaList;
    String filter;
    public boolean passesFilter;
    public String namePass;
    public LinearLayout lTopBar;
    public int visiblePages;

    public CardSwipeAdapter(Context context, List<String> list, boolean vidVisible, String filter) {
        this.context = context;
        this.list = list;
        this.vidVisible = vidVisible;
        this.filter = filter;
        LayoutID = R.layout.card_layout_x;
        passesFilter = true;
        page = 1;
        visiblePages = 1;
    }

    public void setVidVisible(boolean v){
        vidVisible = v;
    }

    public void setFilter(String f){
        filter = f;
    }

    public String getFilter(){
        return filter;
    }

    public boolean doesPassFilter(){
        return passesFilter;
    }

    public String getNamePass(){
        return namePass;
    }

    @Override
    public int getCount() {
        return list.size(); //fix?
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.e(TAG, "Getting view");
        // Log.e(TAG, "Getting view " + position + " and vis is " + vidVisible);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(LayoutID, parent, false);
            getCardUser(position, view);
        } else {
            view = convertView;
            Log.e(TAG, "Old view");
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setup(User user, View v, int pos){
        tvCardName = v.findViewById(R.id.tvCardName);
        tvCardName.setText(user.getName());
        ivCardPic = v.findViewById(R.id.ivCardPic);
        lTopBar = v.findViewById(R.id.lTopBar);
        //Glide.with(v).load(user.getAvatar()).circleCrop().into(ivCardPic);
        try {
            setPFP(user, v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ivInst1 = v.findViewById(R.id.ivInst1);
        ivInst2 = v.findViewById(R.id.ivInst2);
        ivInst3 = v.findViewById(R.id.ivInst3);
        rb1 = v.findViewById(R.id.rb1);
        rb2 = v.findViewById(R.id.rb2);
        rb3 = v.findViewById(R.id.rb3);
        iv1 = v.findViewById(R.id.iv1);
        iv2 = v.findViewById(R.id.iv2);
        iv3 = v.findViewById(R.id.iv3);
        iv4 = v.findViewById(R.id.iv4);
        iv5 = v.findViewById(R.id.iv5);
        iv6 = v.findViewById(R.id.iv6);
        iv7 = v.findViewById(R.id.iv7);
        tvLocation = v.findViewById(R.id.tvLocation);
        tvLocation.setText(popLocation(user));
        tvAge = v.findViewById(R.id.tvAge);
        tvAge.setText(popAge(user));
        vFile = v.findViewById(R.id.vFile);
        populateInstruments(user, v);
        initPageBar();
        showFirst(user, v);
    }

    public void setupHide(User user, View v, int pos){
        tvCardName = v.findViewById(R.id.tvCardName);
        tvCardName.setText(user.getName());
        ivCardPic = v.findViewById(R.id.ivCardPic);
        lTopBar = v.findViewById(R.id.lTopBar);
        //Glide.with(v).load(user.getAvatar()).circleCrop().into(ivCardPic); //COCKATOO
        //pos = list.get(pos).
        try {
            setPFP(user, v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ivInst1 = v.findViewById(R.id.ivInst1);
        ivInst2 = v.findViewById(R.id.ivInst2);
        ivInst3 = v.findViewById(R.id.ivInst3);
        rb1 = v.findViewById(R.id.rb1);
        rb2 = v.findViewById(R.id.rb2);
        rb3 = v.findViewById(R.id.rb3);
        rb1 = v.findViewById(R.id.rb1);
        rb2 = v.findViewById(R.id.rb2);
        rb3 = v.findViewById(R.id.rb3);
        iv1 = v.findViewById(R.id.iv1);
        iv2 = v.findViewById(R.id.iv2);
        iv3 = v.findViewById(R.id.iv3);
        iv4 = v.findViewById(R.id.iv4);
        iv5 = v.findViewById(R.id.iv5);
        iv6 = v.findViewById(R.id.iv6);
        iv7 = v.findViewById(R.id.iv7);
        tvLocation = v.findViewById(R.id.tvLocation);
        tvLocation.setText(popLocation(user));
        tvAge = v.findViewById(R.id.tvAge);
        vFile = v.findViewById(R.id.vFile);
        populateInstruments(user, v);
        initPageBar();
        hideFirst(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String popAge(User user) {
        int age = -1;
        try {
            String bday = user.getMetadata().getString("Birthday");
            age = getAge(bday);
        } catch (JSONException | java.text.ParseException e) {
            e.printStackTrace();
        }
        if(age == -1){
            return "";
        }
        return age + "yo";
    }

    public String popLocation(User user) {
        try {
            double lat = user.getMetadata().getDouble("Lat");
            double lon = user.getMetadata().getDouble("Lon");
            return getCityState(lat, lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "broke";
    }

    public int getAge(String date) throws java.text.ParseException { //must be in mm/dd/yyyy
        if(date.isEmpty()){
            return -1;
        }
        if(date.charAt(date.length()-1) == 'Y') {
            // Log.e(TAG, "Missing date");
            return -1;
        }
        SimpleDateFormat formatter =new SimpleDateFormat("MM/dd/yyyy");
        Date dDate = formatter.parse(date);
        Date currDate = Calendar.getInstance().getTime();
        // Log.e(TAG, "Date: " + dDate);
        // Log.e(TAG, "Current date: " + currDate);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        int years = Integer.parseInt(simpleDateformat.format(currDate))- Integer.parseInt(simpleDateformat.format(dDate));
        if(years < 12 || years > 100){
            // Log.e(TAG, "Please enter a valid date");
            return -1;
        }
        Log.e(TAG, "Age: " + years);
        return years;
    }

    private void setPFP(User user, View v) throws JSONException {
        String pfpURl = user.getMetadata().getString("PFP");
        Log.e(TAG, user.getName() + " has pfp " + pfpURl); //get pfp from metadata
        if(pfpURl.isEmpty()){
            Glide.with(v).load(R.drawable.circle).circleCrop().into(ivCardPic);
        } else {
            Glide.with(v).load(pfpURl).circleCrop().into(ivCardPic);
        }
    }

    private String getCityState(double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        if(lat == 0 && lon == 0){
            return "No Location";
        }

        try {
            addresses = geocoder. getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            Log.e(TAG, "Location city: " + city);
            Log.e(TAG, "Location state: " + state);
            if(city==null || state==null){
                return "No Location";
            }
            return city + ", " + toStateCode(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String toStateCode(String state){
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","QC");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");
        return states.get(state);
    }

    public void setVisiblePages(int n){
        visiblePages = n+1;
    }

    public void clearPageBar(){
        iv1.setColorFilter(null);
        iv2.setColorFilter(null);
        iv3.setColorFilter(null);
        iv4.setColorFilter(null);
        iv5.setColorFilter(null);
        iv6.setColorFilter(null);
        iv7.setColorFilter(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initPageBar(int newPage){
        // Log.e(TAG, "Initializing top bar with " + visiblePages + " pages");
        iv1.setVisibility(View.GONE);
        iv2.setVisibility(View.GONE);
        iv3.setVisibility(View.GONE);
        iv4.setVisibility(View.GONE);
        iv5.setVisibility(View.GONE);
        iv6.setVisibility(View.GONE);
        iv7.setVisibility(View.GONE);
        if(visiblePages >= 1){
            iv1.setVisibility(View.VISIBLE);
            //iv1.setColorFilter(Color.argb(100, 255, 0, 0));
        }
        if(visiblePages >= 2){
            iv2.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 3){
            iv3.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 4){
            iv4.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 5){
            iv5.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 6){
            iv6.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 7){
            iv7.setVisibility(View.VISIBLE);
        }
        updateTopMenu(newPage);
    }

    public void initPageBar(){
        // Log.e(TAG, "Initializing top bar with " + visiblePages + " pages");
        iv1.setVisibility(View.GONE);
        iv2.setVisibility(View.GONE);
        iv3.setVisibility(View.GONE);
        iv4.setVisibility(View.GONE);
        iv5.setVisibility(View.GONE);
        iv6.setVisibility(View.GONE);
        iv7.setVisibility(View.GONE);
        if(visiblePages >= 1){
            iv1.setVisibility(View.VISIBLE);
            //iv1.setColorFilter(Color.argb(100, 255, 0, 0));
        }
        if(visiblePages >= 2){
            iv2.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 3){
            iv3.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 4){
            iv4.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 5){
            iv5.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 6){
            iv6.setVisibility(View.VISIBLE);
        }
        if(visiblePages >= 7){
            iv7.setVisibility(View.VISIBLE);
        }
        updateTopMenu(page);
    }

    public void getCardUser(int pos, View v){
        //Log.e(TAG, "Getting card user");
        // Log.e(TAG, "User at " + pos + " is " + list.get(pos));
        CometChat.getUser(list.get(pos), new CometChat.CallbackListener<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(User user) {
                if(vidVisible){
                    setup(user, v, pos);
                }
                else{
                    setupHide(user, v, pos);
                }
                listeners(pos, user);
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "No user to populate card");
            }
        });
    }

    public void populateInstruments(User user, View v){
        try {
            JSONArray SkillArr = user.getMetadata().getJSONArray("Skills");
            if(SkillArr.length() >= 1){
                ivInst1.setVisibility(View.VISIBLE);
                rb1.setVisibility(View.VISIBLE);
                rb1.setRating(matchLevel(SkillArr.get(0).toString()));
                Glide.with(v).load(matchInst(SkillArr.get(0).toString())).into(ivInst1);
            }
            if(SkillArr.length() >= 2){
                ivInst2.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                rb2.setRating(matchLevel(SkillArr.get(1).toString()));
                Glide.with(v).load(matchInst(SkillArr.get(1).toString())).into(ivInst2);
            }
            else{
                ivInst2.setVisibility(View.INVISIBLE);
                rb2.setVisibility(View.INVISIBLE);
            }
            if(SkillArr.length() >= 3){
                ivInst3.setVisibility(View.VISIBLE);
                rb3.setVisibility(View.VISIBLE);
                rb3.setRating(matchLevel(SkillArr.get(2).toString()));
                Glide.with(v).load(matchInst(SkillArr.get(2).toString())).into(ivInst3);
            }
            else {
                ivInst3.setVisibility(View.INVISIBLE);
                rb3.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Drawable matchInst(String inst){
        if(inst.contains("bass")){
            return context.getResources().getDrawable(R.drawable.bass);
            //Log.e(TAG, "bass");
        }
        if(inst.contains("drums")){
            return context.getResources().getDrawable(R.drawable.drums);
            //Log.e(TAG, "drums");
        }
        if(inst.contains("guitar")){
            return context.getResources().getDrawable(R.drawable.guitar);
            //Log.e(TAG, "guitar");
        }
        if(inst.contains("vocals")){
            return context.getResources().getDrawable(R.drawable.mic);
            //Log.e(TAG, "vocals");
        }
        if(inst.contains("keys")){
            return context.getResources().getDrawable(R.drawable.keys);
            //Log.e(TAG, "keys");
        }
        notifyDataSetChanged();
        return context.getResources().getDrawable(R.drawable.drums);
    }

    public int matchLevel(String inst){
        if(inst.contains("1")){
            return 1;
        }
        if(inst.contains("2")){
            return 2;
        }
        if(inst.contains("3")){
            return 3;
        }
        return 0;
    }

    public void listeners(int pos, User user){
        vFile.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start");
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void hideFirst(User user){
        Log.e(TAG, "hiding");
        tvCardName.setVisibility(View.INVISIBLE);
        ivCardPic.setVisibility(View.INVISIBLE);
        ivInst1.setVisibility(View.INVISIBLE);
        ivInst2.setVisibility(View.INVISIBLE);
        ivInst3.setVisibility(View.INVISIBLE);
        rb1.setVisibility(View.INVISIBLE);
        rb2.setVisibility(View.INVISIBLE);
        rb3.setVisibility(View.INVISIBLE);
        tvAge.setVisibility(View.INVISIBLE);
        tvLocation.setVisibility(View.INVISIBLE);

        //show
        vFile.setVisibility(View.VISIBLE);
        lTopBar.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
        //queryMedia(user);
        //vFile.setVideoPath();
    }

    public void showFirst(User user, View v){
        tvCardName.setVisibility(View.VISIBLE);
        ivCardPic.setVisibility(View.VISIBLE);
        populateInstruments(user, v);
        tvAge.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);
        lTopBar.setVisibility(View.VISIBLE);

        //things to hide
        vFile.setVisibility(View.INVISIBLE);
    }

    public int queryMedia(String user) throws ParseException {
        //Log.e(TAG, user + "Querying");
        ParseQuery<MyMedia> query = ParseQuery.getQuery(MyMedia.class);
        //query.include(MyMedia.KEY_USER);
        query.setLimit(6);
        query.addDescendingOrder("createdAt");
        query.whereContains("User", user);
        // start a synchronous call for posts
        List<MyMedia> vids = query.find();
        Log.e(TAG, user + " has #vids: " + vids.size());
        notifyDataSetChanged();
        mediaList = vids;
        return vids.size();
    }


    public void loadVideo(MyMedia vid){
        String mediaUrl = vid.getVidURL();
        vFile.setVideoPath(mediaUrl);
        vFile.requestFocus();
        vFile.start();
        Log.e(TAG, "Loading vid");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setPage(int p){
        page = p;
        Log.e(TAG, "Page adapter: " + page);
        initPageBar(p);
        if (page > 1) {
            loadVideo(mediaList.get(page-2));
            loadVideo(mediaList.get(page-2));
        }
    }

    public void updateTopMenu(int p) {
        //Log.e(TAG, "Updating menu to page " + p);
        //clearPageBar();
        if(p == 1){
            iv1.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 2){
            //Log.e(TAG, "here page " + p);
            iv2.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 3){
            iv3.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 4){
            iv4.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 5){
            iv5.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 6){
            iv6.setColorFilter(Color.argb(100, 10, 10, 10));
        }
        if(p == 7){
            iv7.setColorFilter(Color.argb(100, 10, 10, 10));
        }
    }

    public void test(int p){
        if (page > 1) {
            loadVideo(mediaList.get(page-2));
        }
    }

}
