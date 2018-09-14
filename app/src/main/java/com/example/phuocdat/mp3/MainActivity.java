package com.example.phuocdat.mp3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SeekBar sb_progress_music;
    private TextView tv_time_start;
    private TextView tv_time_end;
    private ImageView im_play;
    private ImageView im_stop;
    private MyService myService;
    private boolean mBound=false;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private RemoteViews remoteView;
    public final int NOTIFICATION_MEDIA_PLAY=100;

    private ServiceConnection mConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder= (MyService.LocalBinder) service;
            myService=binder.getService();
            mBound=true;
            MainActivity.this.onServiceConnected();
            Log.e("mConnnection","Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound=false;
            Log.e("mConnnection","Disconnected");
        }
    };
    public void initView(){
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_time_end = findViewById(R.id.tv_time_end);
        im_play = findViewById(R.id.img_play);
        im_stop = findViewById(R.id.img_stop);
        sb_progress_music = findViewById(R.id.sb_progress_music);

    }
    public void createMediaNotification(){
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteView=new RemoteViews(getPackageName(),R.layout.media_notification_main);
        remoteView.setImageViewResource(R.id.notification_im_foreground,R.drawable.video_player);
        remoteView.setTextViewText(R.id.notification_tv_name_song,"ten bai hat 1");

        Intent intent_notification_im_start = new Intent("media_notification_start");
        Intent intent_notification_im_stop  = new Intent("music_notification_stop");

        intent_notification_im_start.putExtra("notification_action","start");
        intent_notification_im_stop.putExtra("notification_action","stop");

        PendingIntent p_intent_notification_im_start = PendingIntent.getBroadcast(this,1,
                intent_notification_im_start,0);
        PendingIntent p_intent_notification_im_stop = PendingIntent.getBroadcast(this,2,
                intent_notification_im_stop,0);

        remoteView.setOnClickPendingIntent(R.id.notification_im_start,p_intent_notification_im_start);
        remoteView.setOnClickPendingIntent(R.id.notification_im_stop,p_intent_notification_im_stop);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity","onCreate");
        setContentView(R.layout.activity_main);
        initView();
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
        createMediaNotification();
        showNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity","onStart");
    }


    void onServiceConnected() {
        myService.createMusic(R.raw.nhac);

        SeekBar.OnSeekBarChangeListener listener_seekbar_progress= new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { // User Dragging
                    progress = progress - (progress % 1000);
                    if (myService.getmServiceMediaPlay()!=null) { // if service is running
                        myService.getmServiceMediaPlay().seekTo(progress);
                        sb_progress_music.setProgress(progress);
                        if (!myService.getmServiceMediaPlay().isPlaying()) { // if media Play is stopping
                                myService.stopMusic();
                                myService.startMusic();
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        View.OnClickListener listener_start=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.startMusic();
                showNotification();

            }
        };
        View.OnClickListener listener_stop=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myService.pauseMusic();
                hideNotification();
            }
        };
        //cach 1
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(myService!=null) {
                    long timeStart= System.currentTimeMillis();
                    if (myService.getmServiceMediaPlay()!=null) {
                        int current=myService.getmServiceMediaPlay().getCurrentPosition();
                        sb_progress_music.setProgress(current);
                        Log.d("Current: ", current+ "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int minute= myService.getmServiceMediaPlay().getCurrentPosition()/60000;
                                int second= (myService.getmServiceMediaPlay().getCurrentPosition()/1000)%60;
                                if (second<10)
                                {
                                    tv_time_start.setText(minute + ":0" + second); }
                                else {
                                    tv_time_start.setText(minute + ":" + second); }
                            }
                        });
                    }
                    try {
                        Thread.sleep(1000-(System.currentTimeMillis()-timeStart));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        // cach 2
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.postDelayed(this, 1000);
//                if (media_play.isPlaying()) {
//                    sb_progress_music.setProgress(media_play.getCurrentPosition());
//                    Log.d("Current: ", media_play.getCurrentPosition() + "");
//                }
//            }
//        }, 1000);

        // cach 3
//        Timer timer=new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (myService.getmServiceMediaPlay()!=null) {
//                    int current=myService.getmServiceMediaPlay().getCurrentPosition();
//                    sb_progress_music.setProgress(current);
//                    //Log.d("Current: ", current+ "");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            int minute= myService.getmServiceMediaPlay().getCurrentPosition()/60000;
//                            int second= (myService.getmServiceMediaPlay().getCurrentPosition()/1000)%60;
//                            if (second<10)
//                            {
//                                tv_time_start.setText(minute + ":0" + second);
//                            }
//                            else {
//                                tv_time_start.setText(minute + ":" + second);
//                            }
//                        }
//                    });
//                }
//            }
//        },0,1000);
        int minute= myService.getmServiceMediaPlay().getDuration()/60000;
        int second= (myService.getmServiceMediaPlay().getDuration()/1000)%60;
        tv_time_start.setText("0:00");
        tv_time_end.setText(minute+":"+second);

        sb_progress_music.setMax(myService.getmServiceMediaPlay().getDuration());
        sb_progress_music.setOnSeekBarChangeListener(listener_seekbar_progress);
        // Set Image Clickable
        im_play.setOnClickListener(listener_start);
        im_stop.setOnClickListener(listener_stop);
    }

    public void showNotification()
    {
        Context context=this;
        Intent notification_intent=new Intent(context,MainActivity.class);
        PendingIntent p_notification_intent=PendingIntent.getActivities(context,NOTIFICATION_MEDIA_PLAY,
                new Intent[]{notification_intent},0);
        notificationBuilder=new Notification.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteView)
                .setContentIntent(p_notification_intent);
        notificationManager.notify(NOTIFICATION_MEDIA_PLAY,notificationBuilder.build());
        //myService.startForeground(NOTIFICATION_MEDIA_PLAY,notificationBuilder.build());
    }

    public void hideNotification(){
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_MEDIA_PLAY); }

    @Override
    protected void onStop() {
        Log.e("mConnection", "onStop");
        Log.e("MainActivity","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("MainActivity","onDestroy");
        myService.setmServiceMediaPlay(null);
        myService=null;
        unbindService(mConnection);
        mBound=false;
        super.onDestroy();
    }
}









