package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

/*import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;*/

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;


public class GenreActivity extends AppCompatActivity {
    String TAG = "GenreAct";
    JSONObject metadata;
    private static final int REQUEST_CODE = 1337;
    private String REDIRECT_URI = "http://localhost:8888/callback";
    public static String AUTH_TOKEN = "AUTH_TOKEN";
    public static String CLIENT_ID = "d8b8e2e7993847dcb1fcbe44505de675";
    public static String CLIENT_ID_SECRET = "3585eb11665b4336a95fe19a9e2aea04";
    Button bSpotify;
    TextView tvGenre;
    private static final String ENDPOINT = "https://api.spotify.com/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        initMetadata();
        setup();
        listeners();
    }

    //@Override
    /*protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
    }*/

    private void connected() {
        // Then we will write some more code here.
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    public void initMetadata(){
        String mString = Parcels.unwrap(getIntent().getParcelableExtra("metadata"));
        try {
            metadata = new JSONObject(mString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, String.valueOf(metadata));
    }

    public void setup(){
        bSpotify = findViewById(R.id.bSpotify);
        tvGenre = findViewById(R.id.tvGenre);
    }

    public void listeners(){
        bSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openLoginWindow();
            }
        });

        tvGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GenreSelect.class);
                i.putExtra("metadata", Parcels.wrap(metadata.toString()));
                startActivity(i);
            }
        });
    }

    private void openLoginWindow() {

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        builder.setShowDialog(true);
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    // Handle successful response
                    Log.e(TAG,"Auth token: " + response.getAccessToken());
                    Log.e(TAG,"Auth full response: " + response.describeContents());
                    //getUserAuth();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    //@Override

    /*protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE)

        {

            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                // Response was successful and contains auth token

                case TOKEN:
                    AUTH_TOKEN = response.getAccessToken();
                    Log.e(TAG,"Auth token: " + response.getAccessToken());
                    Log.e(TAG, "GOT AUTH TOKEN");
                    waitForUserInfo();
                    break;

                case ERROR:

                    Log.e(TAG,"Auth error: " + response.getError());
                    break;

                default:
                    Log.d(TAG,"Auth result: " + response.getType());
            }
        }

    }*/

    /*private void waitForUserInfo() {
        get(() -> {
            //editor = getSharedPreferences("SPOTIFY", 0).edit();
            //editor.putString("userid", user.id);
            //Log.e(TAG, "ID: " + user.id);
            Log.e(TAG, "ID: ");
            // We use commit instead of apply because we need the information stored immediately
            //editor.commit();
        });
        Log.e(TAG, "ID got");
    }*/

    /*public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {
            //Gson gson = new Gson();
            Log.e("Testing", response.toString());
            //user = gson.fromJson(response.toString(), User.class);
            callBack.onSuccess();
        }, error -> get(() -> {
            Log.e("Testing", String.valueOf(error));
        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = AUTH_TOKEN;
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }*/

    public interface VolleyCallBack {

        void onSuccess();
    }


}