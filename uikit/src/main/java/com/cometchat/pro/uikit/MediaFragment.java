package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaFragment extends Fragment {
    User me = CometChat.getLoggedInUser();
    String TAG = "MediaFrag";
    public List<MyMedia> mediaList;
    MediaController controller;
    public VideoView vv1;
    public CardView cv1;
    public VideoView vv2;
    public CardView cv2;
    public VideoView vv3;
    public CardView cv3;
    public VideoView vv4;
    public CardView cv4;
    public VideoView vv5;
    public CardView cv5;
    public VideoView vv6;
    public CardView cv6;
    public Button bDel1;
    public Button bDel2;
    public Button bDel3;
    public Button bDel4;
    public Button bDel5;
    public Button bDel6;
    public int clickOne = -1;
    ParseUser currentUser;
    Drawable backgroundOff;

    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_media, container, false);
        try {
            setupView(v, queryMedia());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        listeners();
        return v;
    }

    public int queryMedia() throws ParseException {
        //Log.e(TAG, user + "Querying");
        ParseQuery<MyMedia> query = ParseQuery.getQuery(MyMedia.class);
        //query.include(MyMedia.KEY_USER);
        query.setLimit(6);
        query.addAscendingOrder("createdAt");
        query.whereContains("User", me.getUid());
        // start a synchronous call for posts
        List<MyMedia> vids = query.find();
        Log.e(TAG, me + " has #vids: " + vids.size());
        mediaList = vids;
        return vids.size();
    }

    public void setupView(View v, int vids){
        //backgroundOff = v.getBackground();
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        currentUser=ParseUser.getCurrentUser();
        vv1 = v.findViewById(R.id.vv1);
        cv1 = v.findViewById(R.id.cv1);
        bDel1 = v.findViewById(R.id.bDel1);
        vv2 = v.findViewById(R.id.vv2);
        cv2 = v.findViewById(R.id.cv2);
        bDel2 = v.findViewById(R.id.bDel2);
        vv3 = v.findViewById(R.id.vv3);
        cv3 = v.findViewById(R.id.cv3);
        bDel3 = v.findViewById(R.id.bDel3);
        vv4 = v.findViewById(R.id.vv4);
        cv4 = v.findViewById(R.id.cv4);
        bDel4 = v.findViewById(R.id.bDel4);
        vv5 = v.findViewById(R.id.vv5);
        cv5 = v.findViewById(R.id.cv5);
        bDel5 = v.findViewById(R.id.bDel5);
        vv6 = v.findViewById(R.id.vv6);
        cv6 = v.findViewById(R.id.cv6);
        bDel6 = v.findViewById(R.id.bDel6);
        hideAll();
        notClickable();
        playMedia(vids);
    }

    public void notClickable(){
        cv1.setClickable(false);
        cv2.setClickable(false);
        cv3.setClickable(false);
        cv4.setClickable(false);
        cv5.setClickable(false);
        cv6.setClickable(false);
    }

    public void hideAll(){
        vv1.setVisibility(View.GONE);
        vv1.stopPlayback();
        bDel1.setVisibility(View.GONE);
        vv2.setVisibility(View.GONE);
        vv2.stopPlayback();
        bDel2.setVisibility(View.GONE);
        vv3.setVisibility(View.GONE);
        vv3.stopPlayback();
        bDel3.setVisibility(View.GONE);
        vv4.setVisibility(View.GONE);
        vv4.stopPlayback();
        bDel4.setVisibility(View.GONE);
        vv5.setVisibility(View.GONE);
        vv5.stopPlayback();
        bDel5.setVisibility(View.GONE);
        vv6.setVisibility(View.GONE);
        vv6.stopPlayback();
        bDel6.setVisibility(View.GONE);
    }

    public void playMedia(int vids){
        Log.e(TAG, "Media list size: " + mediaList.size() + ", vids: " + vids);
        //Log.e(TAG, "Media list 0: " + mediaList.get(0).getVidURL() + ", vids: " + vids);
        if(vids >= 1){
            playVid(vv1, bDel1, Uri.parse(mediaList.get(0).getVidURL()));
            clickOne = 2;
        } else {
            clickOne = 1;
        }
        if(vids >= 2){
            playVid(vv2, bDel2, Uri.parse(mediaList.get(1).getVidURL()));
            clickOne = 3;
        }
        if(vids >= 3){
            playVid(vv3, bDel3, Uri.parse(mediaList.get(2).getVidURL()));
            clickOne = 4;
        }
        if(vids >= 4){
            playVid(vv4,bDel4, Uri.parse(mediaList.get(3).getVidURL()));
            clickOne = 5;
        }
        if(vids >= 5){
            playVid(vv5, bDel5,Uri.parse(mediaList.get(4).getVidURL()));
            clickOne = 6;
        }
        if(vids >= 6){
            playVid(vv6, bDel6,Uri.parse(mediaList.get(5).getVidURL()));
            clickOne = -1;
        }
        setClickVisual();
        Log.e(TAG, "Clickone: " + clickOne);
    }

    public void setClickVisual(){
        ColorStateList white = new ColorStateList(new int[][]{{}}, new int[]{Color.WHITE});
        ColorStateList gray = new ColorStateList(new int[][]{{}}, new int[]{Color.LTGRAY});
        backgroundOff = getContext().getResources().getDrawable(R.drawable.blank);
        if(clickOne == 1){
            cv1.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv1.setBackgroundTintList(gray);
        } else {
            cv1.setBackground(backgroundOff);
            cv1.setBackgroundTintList(white);
            cv1.setCardElevation(0);
        }
        if(clickOne == 2){
            cv2.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv2.setBackgroundTintList(gray);
        } else {
            cv2.setBackground(backgroundOff);
            cv2.setBackgroundTintList(white);
            cv2.setCardElevation(0);
        }
        if(clickOne == 3){
            cv3.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv3.setBackgroundTintList(gray);
        } else {
            cv3.setBackground(backgroundOff);
            cv3.setBackgroundTintList(white);
            cv3.setCardElevation(0);
        }
        if(clickOne == 4){
            cv4.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv4.setBackgroundTintList(gray);
        } else {
            cv4.setBackground(backgroundOff);
            cv4.setBackgroundTintList(white);
            cv4.setCardElevation(0);
        }
        if(clickOne == 5){
            cv5.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv5.setBackgroundTintList(gray);
        } else {
            cv5.setBackground(backgroundOff);
            cv5.setBackgroundTintList(white);
            cv5.setCardElevation(0);
        }
        if(clickOne == 6){
            cv6.setBackground(getContext().getResources().getDrawable(R.drawable.plus));
            cv6.setBackgroundTintList(gray);
        } else {
            cv6.setBackground(backgroundOff);
            cv6.setBackgroundTintList(white);
            cv6.setCardElevation(0);
        }
    }

    public void listeners(){
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 1){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 2){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 3){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 4){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 5){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        cv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickOne == 6){
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("video/*");
                    startActivityForResult(i, 45); //for video
                }
            }
        });

        vv1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start1");
                mediaPlayer.setLooping(true);
            }
        });

        vv2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start2");
                mediaPlayer.setLooping(true);
            }
        });

        vv3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start3");
                mediaPlayer.setLooping(true);
            }
        });

        vv4.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start4");
                mediaPlayer.setLooping(true);
            }
        });

        vv5.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start5");
                mediaPlayer.setLooping(true);
            }
        });

        vv6.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "start6");
                mediaPlayer.setLooping(true);
            }
        });
        bDel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        bDel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        bDel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        bDel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(3);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        bDel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(4);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        bDel6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteVideo(5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void playVid(VideoView view, Button bDel, Uri uri){
        view.setVideoURI(uri);
        view.setVisibility(View.VISIBLE);
        bDel.setVisibility(View.VISIBLE);
        view.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 45 && resultCode == -1){
            Uri vid = data.getData();
            if(vid != null){
                Log.e(TAG, "Uri: " + vid);
                byte[] bytes = convertVideoToBytes(vid);
                Log.e(TAG, "Bytes: " + String.valueOf(bytes == null));
                ParseFile videoF = new ParseFile("video.mp4", bytes);
                Log.e(TAG, "ParseFile: " + String.valueOf(videoF == null));
                videoF.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Error saving parsefile " + e);
                            return;
                        }
                        Log.e(TAG, "Parsefile for video saved");
                        saveMedia(me.getUid(), videoF, vid);
                    }
                });
            }
        }
    }

    public byte[] convertVideoToBytes(Uri videoUri) {
        byte[] videoBytes = null;
        //File inputFile=new File(videoUri);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            InputStream is = getContext().getContentResolver().openInputStream(videoUri);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = is.read(buf)))
                baos.write(buf, 0, n);

            videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoBytes;
    }

    private void saveMedia(String UID, ParseFile videoFile, Uri vid) {
        MyMedia video = new MyMedia();
        video.setUser(UID);
        video.setVideo(videoFile);
        video.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving media: " + e);
                    Log.e(TAG, "Video too large!");
                    return;
                }
                Log.e(TAG, "Saved!");
                try {
                    playMedia(queryMedia());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                //whatVid(vid); //sets the pfps
            }
        });
    }

    public void deleteVideo(int pos) throws ParseException {
        MyMedia toDelete = queryToDelete(pos);
        toDelete.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Deleted");
                try {
                    hideAll();
                    playMedia(queryMedia());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public MyMedia queryToDelete(int pos) throws ParseException {
        //Log.e(TAG, user + "Querying");
        ParseQuery<MyMedia> query = ParseQuery.getQuery(MyMedia.class);
        //query.include(MyMedia.KEY_USER);
        query.setLimit(6);
        query.addAscendingOrder("createdAt");
        query.whereContains("User", me.getUid());
        // start a synchronous call for posts
        List<MyMedia> vids = query.find();
        mediaList = vids;
        return vids.get(pos);
    }
}