package com.cometchat.pro.uikit;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.webrtc.CryptoOptions;

import java.io.File;

@ParseClassName("Media")
public class MyMedia extends ParseObject {
    public static final String KEY_USER = "User";
    public static final String KEY_FILE = "File";
    public static final String KEY_URI = "VideoUrl";

    public String TAG = "Media";

    public MyMedia() {
    }

    /*public String getVidURL(){
        return getParseFile(KEY_FILE).getUrl();
    }*/

    public String getVidURL(){
        return getParseFile(KEY_FILE).getUrl();
    }

    public void setVidURL(String uri){
        put(KEY_URI, uri);
    }

    public ParseFile getVideo() {
        return getParseFile(KEY_FILE);
    }

    public void setVideo(ParseFile video) {
        put(KEY_FILE, video);
    }

    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(String user) {
        put(KEY_USER, user);
    }
}
