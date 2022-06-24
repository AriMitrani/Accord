package com.cometchat.pro.uikit.ui_components;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.cometchat.pro.uikit.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class MyProfileFragment extends Fragment {

    public final String TAG = "MyProfile";
    ImageView ivSettings;
    ImageView ivPFP;
    TextView tvName;
    TextView tvBio;
    ImageView ivEditName;
    ImageView ivEditBio;
    TextView tvChangePFP;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

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
        //Log.e(TAG, "PFP url: " + CometChat.getLoggedInUser().getAvatar());
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
        tvChangePFP = v.findViewById(R.id.tvChangePFP);
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
                changePFPDialog();
            }
        });

        tvChangePFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Change PFP");
                changePFPDialog();
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

    public void changePFPDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.pfp_alert, (ViewGroup) getView(), false);
        final Button bCamera = (Button) viewInflated.findViewById(R.id.bCamera);
        final Button bLibrary = (Button) viewInflated.findViewById(R.id.bLibrary);
        alert.setView(viewInflated);

        alert.show();

        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
                //changePFP(newUrl);
                alert.cancel();
            }
        });

        bLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changePFP(newUrl);
                alert.cancel();
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

    public void changePFP(String newUrl){
        //CometChat.getLoggedInUser().setAvatar(newUrl);
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "PFP Updated!");
                Glide.with(getContext()).load(CometChat.getLoggedInUser().getAvatar()).circleCrop().into(ivPFP);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "PFP update failed", e);
            }
        });
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.accord", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    String base64;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                takenImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
                byte[] byteArr = bytes.toByteArray();
                base64 = "data:image/png;base64," + Base64.encodeToString(byteArr, Base64.NO_WRAP);
                //base64 = "data:image/png;base64,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";
                //Log.e(TAG,  base64);
                JSONObject body=new JSONObject();
                try {
                    body.put("avatar", base64);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CometChat.callExtension("avatar", "POST", "/v1/upload", body,
                        new CometChat.CallbackListener < JSONObject > () {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                Log.e(TAG, "Success: " + jsonObject);
                                // {avatarURL: "https://data-us.cometchat.io/avatars/1a2b3c.jpg"}
                            }
                            @Override
                            public void onError(CometChatException e) {
                                Log.e(TAG, "error", e);
                                // Some error occured
                            }
                        });

            } else { // Result was a failure
                Log.e(TAG, "Photo not taken, result: " + resultCode);
            }
        }
    }

}