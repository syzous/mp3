package com.example.phuocdat.mp3;

import android.app.ActivityManager;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private final int NOTIFICATION_MEDIA_PLAY = 100;
    private RotateAnimation rotateAnimation;
    private SeekBar sb_progress_music;
    private TextView tv_time_start;
    private TextView tv_time_end;
    private ImageView im_play;
    private ImageView im_stop;
    private CircleImageView im_song;
    private RemoteViews remoteView;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private static MyService myService;
    private Intent intentService;
    private Context context;
    private boolean mBound = false;

    View.OnClickListener listener_start = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            im_song.startAnimation(rotateAnimation);
            Song song=new Song(R.raw.nhac,"Dung nhu thoi quen","JayKIll",true, 0);
            createMediaNotification(song);
            showNotification();
            if (myService.getmServiceMediaPlay()!=null)
                myService.startMusic();
            else
                myService.createMusic(song.getSongID());
            sb_progress_music.setMax(myService.getmServiceMediaPlay().getDuration());
        }
    };

    View.OnClickListener listener_stop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myService.pauseMusic();
            im_song.setAnimation(null);
            hideNotification();
        }
    };

    SeekBar.OnSeekBarChangeListener listener_seekbar_progress = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) { // User Dragging

                progress = progress - (progress % 1000);

                if (myService.getmServiceMediaPlay() != null) { // if service is running
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            mBound = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (myService != null) {
                        long timeStart = System.currentTimeMillis();


                        if (myService.getmServiceMediaPlay() != null) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int current = myService.getmServiceMediaPlay().getCurrentPosition();
                                    sb_progress_music.setProgress(current);
                                    Log.d("current: ",""+current);
                                    int minute = myService.getCurrentMinute();
                                    int second = myService.getCurrentSecond();
                                    if (second < 10) {
                                        tv_time_start.setText(minute + ":0" + second);
                                    } else {
                                        tv_time_start.setText(minute + ":" + second);
                                    }
                                    int minuteDuration = myService.getmServiceMediaPlay().getDuration() / 60000;
                                    int secondDuration= (myService.getmServiceMediaPlay().getDuration() / 1000) % 60;
                                    tv_time_end.setText(minuteDuration + ":" + secondDuration);

                                }
                            });
                        }
                        try {
                            Thread.sleep(1000 - (System.currentTimeMillis() - timeStart));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
            if (mBound==true) {
                im_play.setOnClickListener(listener_start);
                im_stop.setOnClickListener(listener_stop);
                sb_progress_music.setOnSeekBarChangeListener(listener_seekbar_progress);
            }
            Log.e("mConnnection", "Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.e("mConnnection", "Disconnected");
        }
    };

    public void onServiceConnected() {
        Log.e("onServiceConnected","start");
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
    }

    public void initView() {
        context=getApplicationContext();
        im_song = findViewById(R.id.img_song); // hinh bai hat
        tv_time_start = findViewById(R.id.tv_time_start); // thoi gian bat dau
        tv_time_end = findViewById(R.id.tv_time_end); /// thoi gian ket thuc
        im_play = findViewById(R.id.img_play); /// nut play
        im_stop = findViewById(R.id.img_stop); /// nut stop
        rotateAnimation = new RotateAnimation(0, 360, // su kien xoay
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator()); // xoay khong bi ngat' quang~
        rotateAnimation.setDuration(5000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        sb_progress_music = findViewById(R.id.sb_progress_music); // thanh trang thai cua bai hat
        if (myService!=null) sb_progress_music.setMax(myService.getmServiceMediaPlay().getDuration());
        // Set Image Clickable

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        initView();
        intentService=new Intent(context,MyService.class);
        if (!isMyServiceRunning(intentService.getClass()))
        {
            startService(intentService);
            bindService(intentService, mConnection, BIND_AUTO_CREATE);
            intentService.putExtra("nameSong",R.raw.nhac);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity", "onStart");
    }

    @Override
    protected void onStop() {
        Log.e("MainActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("MainActivity", "onDestroy");
//        if (myService!=null) myService.setmServiceMediaPlay(null);
//        myService = null;
        unbindService(mConnection);
        mBound = false;
//        if (isMyServiceRunning(MyService.class)) stopService(new Intent(this,MyService.class));
        super.onDestroy();
    }

    public void createMediaNotification(Song song) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteView = new RemoteViews(getPackageName(), R.layout.media_notification_main);
        remoteView.setImageViewResource(R.id.notification_im_foreground, R.drawable.video_player);
        remoteView.setTextViewText(R.id.notification_tv_name_song, song.getSongName());

        Intent intent_notification_im_start = new Intent("media_notification_start");
        Intent intent_notification_im_stop = new Intent("music_notification_stop");

        intent_notification_im_start.putExtra("notification_action", "start");
        intent_notification_im_stop.putExtra("notification_action", "stop");

        PendingIntent p_intent_notification_im_start = PendingIntent.getBroadcast(context, 1,
                intent_notification_im_start, 0);
        PendingIntent p_intent_notification_im_stop = PendingIntent.getBroadcast(context, 2,
                intent_notification_im_stop, 0);

        remoteView.setOnClickPendingIntent(R.id.notification_im_start, p_intent_notification_im_start);
        remoteView.setOnClickPendingIntent(R.id.notification_im_stop, p_intent_notification_im_stop);
    }

    public void showNotification() {
        Intent notification_intent = new Intent(context, MainActivity.class);
        PendingIntent p_notification_intent = PendingIntent.getActivities(context, NOTIFICATION_MEDIA_PLAY,
                new Intent[]{notification_intent}, 0);
        notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteView)
                .setContentIntent(p_notification_intent);
        notificationManager.notify(NOTIFICATION_MEDIA_PLAY, notificationBuilder.build());
    }

    public void hideNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_MEDIA_PLAY);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}









