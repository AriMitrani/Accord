package com.cometchat.pro.uikit.ui_components;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.BitmapScaler;
import com.cometchat.pro.uikit.MediaFragment;
import com.cometchat.pro.uikit.ProfPic;
import com.cometchat.pro.uikit.R;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    public final String TAG = "MyProfile";
    ImageView ivSettings;
    ImageView ivPFP;
    ImageView ivButtonMedia;
    TextView tvName;
    TextView tvBio;
    ImageView ivEditName;
    ImageView ivEditBio;
    TextView tvChangePFP;
    TextView tvLoc;
    Uri imageUri;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int PICK_IMAGE = 100;
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

    public void setup(View v) {
        ivSettings = v.findViewById(R.id.ivSettings);
        ivPFP = v.findViewById(R.id.ivPFP);
        setPFP();
        ivButtonMedia = v.findViewById(R.id.ivButtonMedia);
        tvName = v.findViewById(R.id.tvName);
        tvName.setText(CometChat.getLoggedInUser().getName());
        tvBio = v.findViewById(R.id.tvBio);
        if (CometChat.getLoggedInUser().getMetadata() != null) { //it's null if its not initialized, won't be a problem at completion but for now it prevents a crash
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
            return getCityState(lat, lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCityState(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        if (lat == 0 && lon == 0) {
            return "No Location";
        }

        try {
            // Here 1 represent max location result to returned, by documentation it's recommended to use 1 to 5
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            if (city == null || state == null) {
                return "No Location";
            }
            return city + ", " + toStateCode(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String toStateCode(String state) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama", "AL");
        states.put("Alaska", "AK");
        states.put("Alberta", "AB");
        states.put("American Samoa", "AS");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("Armed Forces (AE)", "AE");
        states.put("Armed Forces Americas", "AA");
        states.put("Armed Forces Pacific", "AP");
        states.put("British Columbia", "BC");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Guam", "GU");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Manitoba", "MB");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Brunswick", "NB");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("Newfoundland", "NF");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Northwest Territories", "NT");
        states.put("Nova Scotia", "NS");
        states.put("Nunavut", "NU");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Ontario", "ON");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Prince Edward Island", "PE");
        states.put("Puerto Rico", "PR");
        states.put("Quebec", "QC");
        states.put("Rhode Island", "RI");
        states.put("Saskatchewan", "SK");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virgin Islands", "VI");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
        states.put("Yukon Territory", "YT");
        return states.get(state);
    }

    public void listeners() {
        ivEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog();
            }
        });

        ivEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBioDialog();
            }
        });

        ivPFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePFPDialog();
            }
        });

        tvChangePFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePFPDialog();
            }
        });

        ivButtonMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MediaFragment();
                if (fragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
                }
            }
        });

    }

    public void changeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alert = builder.create();
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
                if (!newName.isEmpty()) {
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
                    e.printStackTrace();
                }
                setPFP();
            }
        });
    }

    private void savePFP(String UID, ParseFile photoFile) {
        ProfPic PFP = new ProfPic();
        PFP.setUser(UID);
        PFP.setImage(photoFile);
        PFP.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                setPFP();
            }
        });
    }

    private void metadataPFP(String fileURL) {
        JSONObject metadata = CometChat.getLoggedInUser().getMetadata();
        try {
            metadata.put("PFP", fileURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void queryPFPs(String UID, File photoFile) {
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        query.include(ProfPic.KEY_USER);
        query.addDescendingOrder("createdAt");
        query.whereContains(ProfPic.KEY_USER, UID);
        query.findInBackground(new FindCallback<ProfPic>() {
            @Override
            public void done(List<ProfPic> pics, ParseException e) {
                // check for errors
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (pics.size() == 0) {
                    savePFP(UID, photoFile);
                } else {
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

    private void queryPFPs(String UID, ParseFile photoFile) {
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        // include data referred by user key
        query.include(ProfPic.KEY_USER);
        // order pfps by creation date (newest first)
        query.addDescendingOrder("createdAt");
        query.whereContains(ProfPic.KEY_USER, UID);
        // start an asynchronous call for pfps
        query.findInBackground(new FindCallback<ProfPic>() {
            @Override
            public void done(List<ProfPic> pics, ParseException e) {
                // check for errors
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (pics.size() == 0) {
                    savePFP(UID, photoFile);
                } else {
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
        ParseQuery<ProfPic> query = ParseQuery.getQuery(ProfPic.class);
        query.include(ProfPic.KEY_USER);
        query.whereContains(ProfPic.KEY_USER, CometChat.getLoggedInUser().getUid());
        // start an asynchronous call for pfp
        query.findInBackground(new FindCallback<ProfPic>() {
            @Override
            public void done(List<ProfPic> pics, ParseException e) {
                // check for errors
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (pics.size() == 0) {
                    //set pfp to default
                    Glide.with(getContext()).load(R.drawable.circle).circleCrop().into(ivPFP);
                } else {
                    //set pfp
                    String pfpFile = pics.get(0).getImage().getUrl();
                    metadataPFP(pfpFile);
                    Glide.with(getContext()).load(pfpFile).circleCrop().into(ivPFP);
                }
            }

        });
    }

    public void changeBioDialog() {
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
                if (!newBio.isEmpty() && newBio.length() <= 140) {
                    changeBio(newBio);
                    alert.cancel();
                }
            }
        });
    }

    public void changePFPDialog() {
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
                alert.cancel();
            }
        });

        bLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                alert.cancel();
            }
        });

    }

    public void changeBio(String bio) {
        JSONObject metadata = CometChat.getLoggedInUser().getMetadata();
        try {
            metadata.put("Bio", bio);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                tvBio.setText(bio);
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
            }
        });
    }

    public void changeName(String name) {
        CometChat.getLoggedInUser().setName(name);
        CometChat.updateCurrentUserDetails(CometChat.getLoggedInUser(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                tvName.setText(name);
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
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
        Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getActivity().getApplicationContext()), "com.example.accord.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getActivity().getApplicationContext()), "com.example.accord.provider", photoFile);
        gallery.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (gallery.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(gallery, PICK_IMAGE);
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
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = getActivity().managedQuery(imageUri, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                switch (orientation) {
                    case 90:
                        takenImage = rotateImage(takenImage, 90);
                        break;
                    case 180:
                        takenImage = rotateImage(takenImage, 180);
                        break;
                    case 270:
                        takenImage = rotateImage(takenImage, 270);
                        break;
                    default:
                        break;
                }
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 1000);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized");
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
                queryPFPs(CometChat.getLoggedInUser().getUid(), photoFile);
            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    imageUri = data.getData();
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = getActivity().managedQuery(imageUri, orientationColumn, null, null, null);
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }
                    InputStream inStream = null;
                    try {
                        inStream = getActivity().getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                    switch (orientation) {
                        case 90:
                            bitmap = rotateImage(bitmap, 90);
                            break;
                        case 180:
                            bitmap = rotateImage(bitmap, 180);
                            break;
                        case 270:
                            bitmap = rotateImage(bitmap, 270);
                            break;
                        default:
                            break;
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] imageByte = byteArrayOutputStream.toByteArray();
                    ParseFile parseFile = new ParseFile("pfp.png", imageByte);
                    parseFile.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            // If successful add file to user and signUpInBackground
                            if (null == e) {
                                queryPFPs(CometChat.getLoggedInUser().getUid(), parseFile);
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

}