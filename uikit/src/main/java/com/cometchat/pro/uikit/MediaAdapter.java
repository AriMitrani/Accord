package com.cometchat.pro.uikit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import java.util.List;

public class MediaAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public int page = 1;
    public String TAG = "Card";

    public MediaAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        page = 1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View newView, ViewGroup viewGroup) {
        View view;
        if (newView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_media, viewGroup, false);
            getCardUser(i, view);
            Log.e(TAG, "Null");
        } else {
            view = newView;
            Log.e(TAG, "Old view");
        }
        return view;
    }

    public void getCardUser(int pos, View v) {
        Log.e(TAG, "User at " + pos + " is " + list.get(pos));
        CometChat.getUser(list.get(pos), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "User at " + pos + " is now " + user.getName());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "No user to populate card", e);
            }
        });
    }
}
