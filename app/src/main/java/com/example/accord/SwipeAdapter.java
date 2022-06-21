package com.example.accord;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class SwipeAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> list;
    public final String TAG = "SwipeAdapter";

    public SwipeAdapter(Context context, List<Integer> list) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
            Log.e(TAG, "Null");
        } else {
            view = convertView;
            Log.e(TAG, "Old view");
        }
        return view;
    }
}
