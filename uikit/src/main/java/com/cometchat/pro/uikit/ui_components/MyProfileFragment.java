package com.cometchat.pro.uikit.ui_components;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.BuildConfig;
import com.cometchat.pro.uikit.R;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyProfileFragment extends Fragment {

    public final String TAG = "MyProfile";
    ImageView ivSettings;
    ImageView ivPFP;
    TextView tvName;
    TextView tvBio;
    ImageView ivEditName;
    ImageView ivEditBio;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        setup(v);
        listeners();
        return v;
    }

    public void setup(View v){
        ivSettings = v.findViewById(R.id.ivSettings);
        ivPFP = v.findViewById(R.id.ivPFP);
        Log.e(TAG, "PFP user: " + CometChat.getLoggedInUser().getName());
        Log.e(TAG, "PFP url: " + CometChat.getLoggedInUser().getAvatar());
        if(CometChat.getLoggedInUser().getAvatar() != null){
            Glide.with(this).load(CometChat.getLoggedInUser().getAvatar()).circleCrop().into(ivPFP);
        }
        tvName = v.findViewById(R.id.tvName);
        tvName.setText(CometChat.getLoggedInUser().getName());
        tvBio = v.findViewById(R.id.tvBio);
        if(CometChat.getLoggedInUser().getMetadata() != null){ //it's null if its not initialized, won't be a problem at completion but for now it prevents a crash
            try {
                tvBio.setText(CometChat.getLoggedInUser().getMetadata().getString("Bio"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ivEditName = v.findViewById(R.id.ivEditName);
        ivEditBio = v.findViewById(R.id.ivEditBio);
    }

    public void listeners(){
        ivEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog();
            }
        });

        ivEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Changing bio");
                changeBioDialog();
            }
        });

        ivPFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Change PFP");
            }
        });

    }

    public void changeNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        //builder.setTitle("Change name");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_alert, (ViewGroup) getView(), false);
        TextInputLayout IL = (TextInputLayout) viewInflated.findViewById(R.id.textInputLayout);
        IL.setCounterEnabled(false);
        final EditText etInput = (EditText) viewInflated.findViewById(R.id.etInput);
        etInput.setHint("Name");
        final Button bChange = (Button) viewInflated.findViewById(R.id.bChange);
        bChange.setText("Change name");
        alert.setView(viewInflated);

        alert.show();

        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etInput.getText().toString().trim();
                if(!newName.isEmpty()){
                    Log.e(TAG, newName);
                    changeName(newName);
                    alert.cancel();
                }
            }
        });
    }

    public void changeBioDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_alert, (ViewGroup) getView(), false);
        TextInputLayout IL = (TextInputLayout) viewInflated.findViewById(R.id.textInputLayout);
        IL.setCounterEnabled(true);
        final EditText etInput = (EditText) viewInflated.findViewById(R.id.etInput);
        etInput.setHint("Bio");
        final Button bChange = (Button) viewInflated.findViewById(R.id.bChange);
        bChange.setText("Change bio");
        alert.setView(viewInflated);

        alert.show();

        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newBio = etInput.getText().toString().trim();
                if(!newBio.isEmpty() && newBio.length() <= 140){
                    Log.e(TAG, newBio);
                    changeBio(newBio);
                    alert.cancel();
                }
            }
        });
    }

    public void changeBio(String bio){
        JSONObject metadata = CometChat.getLoggedInUser().getMetadata();
        try {
            metadata.put("Bio", bio);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "Updated!");
                tvBio.setText(bio);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Name update failed", e);
            }
        });
    }

    public void changeName(String name){
        CometChat.getLoggedInUser().setName(name);
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "Updated!");
                tvName.setText(name);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Name update failed", e);
            }
        });
    }

}