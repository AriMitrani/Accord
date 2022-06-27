package com.cometchat.pro.uikit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import java.util.List;

public class CardSwipeAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public final String TAG = "SwipeAdapter";
    public TextView tvCardName;
    public ImageView ivCardPic;

    public CardSwipeAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_x, parent, false);
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
    }

    public void getCardUser(int pos, View v){
        Log.e(TAG, "User at " + pos + " is " + list.get(pos));
        CometChat.getUser(list.get(pos), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                setup(user, v);
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "No user to populate card", e);
            }
        });
    }
}
