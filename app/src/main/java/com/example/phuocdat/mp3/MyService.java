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
    private Looper mServiceLooper;
    private HandlerService mServiceHandler;
    private MediaPlayer mServiceMediaPlay;
    private long currentTime;
    private IBinder mServiceBinder= new LocalBinder();

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

    public class LocalBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    private class HandlerService extends Handler {
        HandlerService(Looper looper) { super(looper); }
        @Override
        public void handleMessage(Message msg) {
            Log.d("msg: ",msg+"");
            //stopSelf(msg.arg1);
        }
    }
    public int currentSong=R.raw.nhac;
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
    public void createMusic(int nameSong){
        if(mServiceMediaPlay == null) {
            mServiceMediaPlay=MediaPlayer.create(this,nameSong);
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
        currentSong=nameSong;
    }
    public void startMusic()
    {

        if (mServiceMediaPlay!=null && !mServiceMediaPlay.isPlaying()){ mServiceMediaPlay.start(); return; }
        if (mServiceMediaPlay==null)
        {
            mServiceMediaPlay=MediaPlayer.create(this,currentSong);
            mServiceMediaPlay.seekTo(0);
            mServiceMediaPlay.start();
        }
    }
    public void startMusic(int time)
    {
        if (mServiceMediaPlay!=null && !mServiceMediaPlay.isPlaying()){ mServiceMediaPlay.start(); mServiceMediaPlay.seekTo(time); return; }
        if (mServiceMediaPlay==null)
        {
            mServiceMediaPlay=MediaPlayer.create(this,currentSong);
            mServiceMediaPlay.seekTo(time);
            mServiceMediaPlay.start();
        }
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
        Log.d("OnStartCommandService: ","Start");

        Message msg= mServiceHandler.obtainMessage();
        msg.arg1=startId;
        mServiceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d("onCreateService: ","Start");
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

    public long getCurrentTime() {
        return currentTime;
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
        stopSelf();
        Log.d("onDestroyService: ","Start");
        super.onDestroy();
    }
}
