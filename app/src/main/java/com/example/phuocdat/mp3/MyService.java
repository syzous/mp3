package com.example.phuocdat.mp3;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class MyService extends Service {

    private int currentSong;
    private Looper mServiceLooper;
    private HandlerService mServiceHandler;
    private static MediaPlayer mServiceMediaPlay;

    private IBinder mServiceBinder= new LocalBinder();

    public int getCurrentMinute(){
        return mServiceMediaPlay.getCurrentPosition()/60000;
    }
    public int getCurrentSecond(){
        return (mServiceMediaPlay.getCurrentPosition() / 1000) % 60;
    }


    public void setmServiceMediaPlay(MediaPlayer mServiceMediaPlay) {
        this.mServiceMediaPlay = mServiceMediaPlay;
    }

    public MediaPlayer getmServiceMediaPlay() {
        return mServiceMediaPlay;
    }
    public Service getService(){
        return  this;
    }

    public IBinder getmServiceBinder() {
        return mServiceBinder;
    }

    private class HandlerService extends Handler {
        HandlerService(Looper looper) { super(looper); }
        @Override
        public void handleMessage(Message msg) {
            Log.e("msg: ",msg+"");

        }
    }
    public class LocalBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    public void createMusic(){
        if(mServiceMediaPlay == null) {
            mServiceMediaPlay=MediaPlayer.create(this,currentSong);
            mServiceMediaPlay.start();
            mServiceMediaPlay.seekTo(0);
            // Music Completed
            mServiceMediaPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mServiceMediaPlay!=null) {
                        mServiceMediaPlay.stop();
                        mServiceMediaPlay.reset();
                        mServiceMediaPlay.release();
                        mServiceMediaPlay = null;
                    }
                }
            });
        }
    }
    public void createMusic(int songName){
        if(mServiceMediaPlay == null) {
            mServiceMediaPlay=MediaPlayer.create(this,songName);
            mServiceMediaPlay.start();
            mServiceMediaPlay.seekTo(0);
            // Music Completed
            mServiceMediaPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mServiceMediaPlay!=null) {
                        mServiceMediaPlay.stop();
                        mServiceMediaPlay.reset();
                        mServiceMediaPlay.release();
                        mServiceMediaPlay = null;
                    }
                }
            });
        }
        currentSong=songName;
    }
    public void startMusic()
    {

        if (mServiceMediaPlay!=null && !mServiceMediaPlay.isPlaying()){ mServiceMediaPlay.start(); return; }
//        if (mServiceMediaPlay==null)
//        {
//            mServiceMediaPlay=MediaPlayer.create(this,currentSong);
//            mServiceMediaPlay.seekTo(0);
//            mServiceMediaPlay.start();
//        }
    }
    public void startMusic(int time)
    {
        if (mServiceMediaPlay!=null && !mServiceMediaPlay.isPlaying()){ mServiceMediaPlay.start(); mServiceMediaPlay.seekTo(time); return; }

    }
    public void pauseMusic()
    {
        if (mServiceMediaPlay!=null && mServiceMediaPlay.isPlaying()) { mServiceMediaPlay.pause(); }
    }
    public void stopMusic()  {
        if (mServiceMediaPlay!=null){
            mServiceMediaPlay.stop();
            try {
                mServiceMediaPlay.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("OnStartCommandService: ","Start");

        Message msg= mServiceHandler.obtainMessage();
        msg.arg1=startId;
        mServiceHandler.sendMessage(msg);

        currentSong= intent.getIntExtra("songName",1);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e("onCreateService: ","Start");
        HandlerThread thread=new HandlerThread("Thread Handler",Thread.NORM_PRIORITY);
        thread.start();

        mServiceLooper=thread.getLooper();
        mServiceHandler =new HandlerService(mServiceLooper);
        super.onCreate();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public void onDestroy() {
        Log.e("MyService","onDestroy()");
        if (mServiceMediaPlay!=null) {
            mServiceMediaPlay.stop();
            mServiceMediaPlay.reset();
            mServiceMediaPlay.release();
            mServiceMediaPlay = null;
        }

        Log.d("onDestroyService: ","Start");
        super.onDestroy();
    }
}
