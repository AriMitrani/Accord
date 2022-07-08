package com.cometchat.pro.uikit.ui_components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

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
import com.cometchat.pro.uikit.BitmapScaler;
import com.cometchat.pro.uikit.BuildConfig;
import com.cometchat.pro.uikit.ProfPic;
import com.cometchat.pro.uikit.R;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    public final String TAG = "MyProfile";
    ImageView ivSettings;
    ImageView ivPFP;
    TextView tvName;
    TextView tvBio;
    ImageView ivEditName;
    ImageView ivEditBio;
    TextView tvChangePFP;
    TextView tvLoc;
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
        /*if(CometChat.getLoggedInUser().getAvatar() != null){
            Glide.with(this).load(CometChat.getLoggedInUser().getAvatar()).circleCrop().into(ivPFP);
        }*/
        setPFP();
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
        tvLoc = v.findViewById(R.id.tvLoc);
        tvLoc.setText(popLocation(CometChat.getLoggedInUser()));
    }

    public String popLocation(User user) {
        try {
            double lat = user.getMetadata().getDouble("Lat");
            double lon = user.getMetadata().getDouble("Lon");
            // Log.e(TAG, "Pop Lat: " + lat);
            return getCityState(lat, lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "broke";
    }

    private String getCityState(double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        if(lat == 0 && lon == 0){
            return "No Location";
        }

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            // String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            // Log.e(TAG, "Address: " + address);
            // Log.e(TAG, "Lat: " + lat);
            // Log.e(TAG, "Location city: " + city);
            // Log.e(TAG, "Location state: " + state);
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

                //queryPFPs(CometChat.getLoggedInUser().getUid(), pfp);
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
        final EditText etInput = (EditText) viewInflated.findViewById(R.id.etBio);
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

    private void savePFP(String UID, File photoFile) {
        ProfPic PFP = new ProfPic();
        PFP.setUser(UID);
        PFP.setImage(new ParseFile(photoFile));
        PFP.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving prof pic: " + e);
                }
                Log.e(TAG, "Saved!");
                //add to metadata
                setPFP();
                //ivPFP.setImageResource(0);
            }
        });
    }

    private void metadataPFP(String fileURL){
        JSONObject metadata = CometChat.getLoggedInUser().getMetadata();
        try {
            metadata.put("PFP", fileURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.e(TAG, "Updated!");
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "PFP update failed", e);
            }
        });
    }

    private void queryPFPs(String UID, File photoFile) {
        //Boolean done = false;
        Log.e(TAG, "Query started");
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        // include data referred by user key
        query.include(ProfPic.KEY_USER);
        //query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        query.whereContains(ProfPic.KEY_USER, UID);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<ProfPic>() {
            @Override
            public void done(List<ProfPic> pics, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting pfp", e);
                    return;
                }
                Log.e(TAG, "# of pics: " + pics.size());
                if(pics.size() == 0) {
                    savePFP(UID, photoFile);
                }
                else{
                    pics.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            savePFP(UID, photoFile);
                        }
                    });
                }
            }

        });
    }

    private void setPFP() {
        Log.e(TAG, "Setting pfp");
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        query.include(ProfPic.KEY_USER);
        query.whereContains(ProfPic.KEY_USER, CometChat.getLoggedInUser().getUid());
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
                    Glide.with(getContext()).load(R.drawable.circle).circleCrop().into(ivPFP);
                }
                else{
                    //set pfp
                    Log.e(TAG, "Setting existing PFP");
                    String pfpFile = pics.get(0).getImage().getUrl();
                    metadataPFP(pfpFile);
                    Glide.with(getContext()).load(pfpFile).circleCrop().into(ivPFP);
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
        final EditText etInput = (EditText) viewInflated.findViewById(R.id.etBio);
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

    /*public void changePFP(String newUrl){
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
    }*/

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getActivity().getApplicationContext()),  "com.example.accord.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            Log.e(TAG, "Starting for result.");
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        //return new File(Environment.getExternalStorageDirectory(), fileName);

        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "Result: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        // Log.e(TAG, "Photo start.");
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Log.e(TAG, "Photo ok.");
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 1000);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
// Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
// Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                //rotateBitmapOrientation(resizedFile.getPath());
                try {
                    resizedFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
// Write the bytes of the bitmap to file
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the taken image into a preview
                // Log.e(TAG, "Photo load.");
                //ivPFP.setImageBitmap(resizedBitmap);
                queryPFPs(CometChat.getLoggedInUser().getUid(), photoFile);
            } else { // Result was a failure
                Log.e(TAG, "Photo not taken code: " + resultCode);
            }
        }
    }

}