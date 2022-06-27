package com.cometchat.pro.uikit;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

@ParseClassName("Media")
public class MyMedia extends ParseObject {
    public static final String KEY_USER = "User";
    public static final String KEY_FILE = "File";

    public String TAG = "Media";

    public MyMedia() {
    }

    public String getVidURL(){
        return getParseFile(KEY_FILE).getUrl();
    }
}
