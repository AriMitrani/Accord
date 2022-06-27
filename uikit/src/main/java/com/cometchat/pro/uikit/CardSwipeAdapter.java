package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class CardSwipeAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public final String TAG = "SwipeAdapter";
    public TextView tvCardName;
    public ImageView ivCardPic;
    public ImageView ivInst1;
    public ImageView ivInst2;
    public ImageView ivInst3;
    public RatingBar rb1;
    public RatingBar rb2;
    public RatingBar rb3;
    public TextView tvLocation;
    public TextView tvAge;
    public int LayoutID;
    public int page;
    public LinearLayout lLeft;
    public LinearLayout lRight;

    public CardSwipeAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        LayoutID = R.layout.card_layout_x;
        page = 1;
    }

    @Override
    public int getCount() {
        return list.size();
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
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(LayoutID, parent, false);
            getCardUser(position, view);
            Log.e(TAG, "Null");
        } else {
            view = convertView;
            Log.e(TAG, "Old view");
        }
        return view;
    }

    public void setup(User user, View v){
        Log.e(TAG, user.toString());
        tvCardName = v.findViewById(R.id.tvCardName);
        tvCardName.setText(user.getName());
        ivCardPic = v.findViewById(R.id.ivCardPic);
        Glide.with(v).load(user.getAvatar()).circleCrop().into(ivCardPic);
        ivInst1 = v.findViewById(R.id.ivInst1);
        ivInst2 = v.findViewById(R.id.ivInst2);
        ivInst3 = v.findViewById(R.id.ivInst3);
        rb1 = v.findViewById(R.id.rb1);
        rb2 = v.findViewById(R.id.rb2);
        rb3 = v.findViewById(R.id.rb3);
        tvLocation = v.findViewById(R.id.tvLocation);
        tvAge = v.findViewById(R.id.tvAge);
        lLeft = v.findViewById(R.id.lLeft);
        lRight = v.findViewById(R.id.lRight);
        populateInstruments(user, v);
        showFirst();
    }

    public void getCardUser(int pos, View v){
        Log.e(TAG, "User at " + pos + " is " + list.get(pos));
        CometChat.getUser(list.get(pos), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                setup(user, v);
                listeners(pos);
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "No user to populate card", e);
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

    public void listeners(int pos){
        lLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Left");
                if(page - 1 == 1) {
                    showFirst();
                    notifyDataSetChanged();
                    page -= 1;
                }
                else if(page != 1) {
                    page -=1;
                }
                Log.e(TAG, "Page: " + page);
            }
        });

        lRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Right");
                if(page == 1) {
                    hideFirst();
                    notifyDataSetChanged();
                }
                page += 1;
                Log.e(TAG, "Page: " + page);
            }
        });
    }

    public void hideFirst(){
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
    }

    public void showFirst(){
        tvCardName.setVisibility(View.VISIBLE);
        ivCardPic.setVisibility(View.VISIBLE);
        ivInst1.setVisibility(View.VISIBLE);
        ivInst2.setVisibility(View.VISIBLE);
        ivInst3.setVisibility(View.VISIBLE);
        rb1.setVisibility(View.VISIBLE);
        rb2.setVisibility(View.VISIBLE);
        rb3.setVisibility(View.VISIBLE);
        tvAge.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);
    }
}
