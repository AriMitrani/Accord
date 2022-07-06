package com.example.accord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InstrumentActivity extends AppCompatActivity {

    private static final String TAG = "Instruments";
    public ConstraintLayout clDrums;
    public ConstraintLayout clGuitar;
    public ConstraintLayout clBass;
    public ConstraintLayout clVocals;
    public ConstraintLayout clKeys;
    public ConstraintLayout clProd;

    public ConstraintLayout clDrums2;
    public ConstraintLayout clGuitar2;
    public ConstraintLayout clBass2;
    public ConstraintLayout clVocals2;
    public ConstraintLayout clKeys2;
    public ConstraintLayout clProd2;

    public Button bDrums;
    public Button bGuitar;
    public Button bBass;
    public Button bVocals;
    public Button bKeys;
    public Button bProd;

    public RadioButton drums1;
    public RadioButton drums2;
    public RadioButton drums3;
    public RadioButton guitar1;
    public RadioButton guitar2;
    public RadioButton guitar3;
    public RadioButton bass1;
    public RadioButton bass2;
    public RadioButton bass3;
    public RadioButton vocals1;
    public RadioButton vocals2;
    public RadioButton vocals3;
    public RadioButton keys1;
    public RadioButton keys2;
    public RadioButton keys3;
    public RadioButton prod1;
    public RadioButton prod2;
    public RadioButton prod3;

    public Button bNext;

    public boolean drums = false;
    public boolean guitar = false;
    public boolean bass = false;
    public boolean vocals = false;
    public boolean keys = false;
    public boolean prod = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);
        setup();
        listeners();
    }

    public void setup(){
        clDrums = findViewById(R.id.clDrum);
        clGuitar = findViewById(R.id.clGuitar);
        clBass = findViewById(R.id.clBass);
        clVocals = findViewById(R.id.clVocals);
        clKeys = findViewById(R.id.clKeys);
        clProd = findViewById(R.id.clProd);
        clDrums.setVisibility(View.VISIBLE);
        clGuitar.setVisibility(View.VISIBLE);
        clBass.setVisibility(View.VISIBLE);
        clVocals.setVisibility(View.VISIBLE);
        clKeys.setVisibility(View.VISIBLE);
        clProd.setVisibility(View.VISIBLE);
        bNext = findViewById(R.id.bNext);
        bNext.setVisibility(View.GONE);

        clDrums2 = findViewById(R.id.clDrum2);
        clDrums2.setVisibility(View.INVISIBLE);
        bDrums = findViewById(R.id.bDrums);
        clGuitar2 = findViewById(R.id.clGuitar2);
        clGuitar2.setVisibility(View.INVISIBLE);
        bGuitar = findViewById(R.id.bGuitar);
        clBass2 = findViewById(R.id.clBass2);
        clBass2.setVisibility(View.INVISIBLE);
        bBass = findViewById(R.id.bBass);
        clVocals2 = findViewById(R.id.clVocals2);
        clVocals2.setVisibility(View.INVISIBLE);
        bVocals = findViewById(R.id.bVocals);
        clKeys2 = findViewById(R.id.clKeys2);
        clKeys2.setVisibility(View.INVISIBLE);
        bKeys = findViewById(R.id.bKeys);
        clProd2 = findViewById(R.id.clProd2);
        clProd2.setVisibility(View.INVISIBLE);
        bProd = findViewById(R.id.bProd);

        drums1 = findViewById(R.id.rbDrums1);
        drums2 = findViewById(R.id.rbDrums2);
        drums3 = findViewById(R.id.rbDrums3);
        guitar1 = findViewById(R.id.rbGuitar1);
        guitar2 = findViewById(R.id.rbGuitar2);
        guitar3 = findViewById(R.id.rbGuitar3);
        bass1 = findViewById(R.id.rbBass1);
        bass2 = findViewById(R.id.rbBass2);
        bass3 = findViewById(R.id.rbBass3);
        vocals1 = findViewById(R.id.rbVocals1);
        vocals2 = findViewById(R.id.rbVocals2);
        vocals3 = findViewById(R.id.rbVocals3);
        keys1 = findViewById(R.id.rbKeys1);
        keys2 = findViewById(R.id.rbKeys2);
        keys3 = findViewById(R.id.rbKeys3);
        prod1 = findViewById(R.id.rbProd1);
        prod2 = findViewById(R.id.rbProd2);
        prod3 = findViewById(R.id.rbProd3);

    }

    public void listeners(){
        clDrums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clDrums.setVisibility(View.INVISIBLE);
                clDrums2.setVisibility(View.VISIBLE);
                drums1.setSelected(true);
                drums1.setChecked(true);
                drums = true;
                updateNext();
            }
        });
        bDrums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clDrums2.setVisibility(View.INVISIBLE);
                clDrums.setVisibility(View.VISIBLE);
                drums1.setSelected(false);
                drums = false;
                updateNext();
            }
        });

        clGuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clGuitar.setVisibility(View.INVISIBLE);
                clGuitar2.setVisibility(View.VISIBLE);
                guitar1.setSelected(true);
                guitar1.setChecked(true);
                guitar = true;
                updateNext();
            }
        });
        bGuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clGuitar2.setVisibility(View.INVISIBLE);
                clGuitar.setVisibility(View.VISIBLE);
                guitar1.setSelected(false);
                guitar = false;
                updateNext();
            }
        });

        clBass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clBass.setVisibility(View.INVISIBLE);
                clBass2.setVisibility(View.VISIBLE);
                bass1.setSelected(true);
                bass1.setChecked(true);
                bass = true;
                updateNext();
            }
        });
        bBass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clBass2.setVisibility(View.INVISIBLE);
                clBass.setVisibility(View.VISIBLE);
                bass1.setSelected(false);
                bass = false;
                updateNext();
            }
        });

        clVocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clVocals.setVisibility(View.INVISIBLE);
                clVocals2.setVisibility(View.VISIBLE);
                vocals1.setSelected(true);
                vocals1.setChecked(true);
                vocals = true;
                updateNext();
            }
        });
        bVocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clVocals2.setVisibility(View.INVISIBLE);
                clVocals.setVisibility(View.VISIBLE);
                vocals1.setSelected(false);
                vocals = false;
                updateNext();
            }
        });

        clKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clKeys.setVisibility(View.INVISIBLE);
                clKeys2.setVisibility(View.VISIBLE);
                keys1.setSelected(true);
                keys1.setChecked(true);
                keys = true;
                updateNext();
            }
        });
        bKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clKeys2.setVisibility(View.INVISIBLE);
                clKeys.setVisibility(View.VISIBLE);
                keys1.setSelected(false);
                keys = false;
                updateNext();
            }
        });

        clProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clProd.setVisibility(View.INVISIBLE);
                clProd2.setVisibility(View.VISIBLE);
                prod1.setSelected(true);
                prod1.setChecked(true);
                prod = true;
                updateNext();
            }
        });
        bProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clProd2.setVisibility(View.INVISIBLE);
                clProd.setVisibility(View.VISIBLE);
                prod1.setSelected(false);
                prod = false;
                updateNext();
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Button metadata click");
                try {
                    createData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public boolean selectedListener(){
        boolean selected = false;
        if(drums1.isSelected()){
            selected = true;
        }
        else if(drums1.isSelected()){
            selected = true;
        }
        else if(drums3.isSelected()){
            selected = true;
        }
        if(guitar1.isSelected()){
            selected = true;
        }
        else if(guitar2.isSelected()){
            selected = true;
        }
        else if(guitar3.isSelected()){
            selected = true;
        }
        if(bass1.isSelected()){
            selected = true;
        }
        else if(bass2.isSelected()){
            selected = true;
        }
        else if(bass3.isSelected()){
            selected = true;
        }
        if(vocals1.isSelected()){
            selected = true;
        }
        else if(vocals2.isSelected()){
            selected = true;
        }
        else if(vocals3.isSelected()){
            selected = true;
        }
        if(keys1.isSelected()){
            selected = true;
        }
        else if(keys2.isSelected()){
            selected = true;
        }
        else if(keys3.isSelected()){
            selected = true;
        }
        if(prod1.isSelected()){
            selected = true;
        }
        else if(prod2.isSelected()){
            selected = true;
        }
        else if(prod3.isSelected()){
            selected = true;
        }
        return selected;
    }

    public void updateNext(){
        boolean selected = selectedListener();
        if (selected) {
            bNext.setVisibility(View.VISIBLE);
        }
        else {
            bNext.setVisibility(View.GONE);
        }
    }

    public void createData() throws JSONException {
        Log.e(TAG, "Creating metadata");
        JSONArray metadata = new JSONArray();
        JSONObject bio = new JSONObject();
        bio.put("Bio", "");
        JSONObject lat = new JSONObject();
        bio.put("Lat", 0);
        JSONObject lon = new JSONObject();
        bio.put("Lon", 0);
        JSONObject deck = new JSONObject();
        JSONArray deckArr = new JSONArray();
        deck.put("Deck", deckArr);
        JSONObject liked = new JSONObject();
        JSONArray likedArr = new JSONArray();
        liked.put("Liked", likedArr);
        JSONObject genres = new JSONObject();
        JSONArray genreArr = new JSONArray();
        genres.put("Genres", genreArr);
        JSONObject skills = new JSONObject();
        JSONArray skillsArr = new JSONArray();
        skills.put("Skills", skillsArr);
        metadata.put(bio);
        metadata.put(lat);
        metadata.put(lon);
        metadata.put(deck);
        metadata.put(liked);
        metadata.put(genres);
        metadata.put(skills);
        Log.e(TAG, "Metadata: " + metadata);

        if(drums1.isSelected()){

        }
        else if(drums1.isSelected()){

        }
        else if(drums3.isSelected()){

        }
        if(guitar1.isSelected()){

        }
        else if(guitar2.isSelected()){

        }
        else if(guitar3.isSelected()){

        }
        if(bass1.isSelected()){

        }
        else if(bass2.isSelected()){

        }
        else if(bass3.isSelected()){

        }
        if(vocals1.isSelected()){

        }
        else if(vocals2.isSelected()){

        }
        else if(vocals3.isSelected()){

        }
        if(keys1.isSelected()){

        }
        else if(keys2.isSelected()){

        }
        else if(keys3.isSelected()){

        }
        if(prod1.isSelected()){

        }
        else if(prod2.isSelected()){

        }
        else if(prod3.isSelected()){

        }
    }
}