package com.cometchat.pro.uikit;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

@ParseClassName("ProfPic")
public class ProfPic extends ParseObject {

    public static final String KEY_IMAGE = "photo";
    public static final String KEY_USER = "UID";
    public String TAG = "PFP";

    public ProfPic() {
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(String user) {
        put(KEY_USER, user);
    }

}
