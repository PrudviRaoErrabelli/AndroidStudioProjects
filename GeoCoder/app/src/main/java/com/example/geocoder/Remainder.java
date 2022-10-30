package com.example.geocoder;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class Remainder extends Service {
    MediaPlayer mediaPlayer;
    public void play(){
        if(mediaPlayer==null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }mediaPlayer.start();
            }
            public void pause(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
            }
            public void stop(){
        stopPlayer();
            }
            public void stopPlayer() {
            if(mediaPlayer!=null){
                mediaPlayer.release();
                mediaPlayer=null;
                Log.d("MediaPlayer","MediaPlayer released");
            }
            }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
