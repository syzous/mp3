package com.example.phuocdat.mp3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ImageNotificationListener extends BroadcastReceiver{

    MyService myService;
    @Override
    public void onReceive(Context context, Intent intent) {
        MyService.LocalBinder binder= (MyService.LocalBinder) peekService(context,new Intent(context,MyService.class));
        if (binder!=null)
        {
            MyService service=binder.getService();

            if (intent.getAction().equals("media_notification_start")) {
                Log.e("ImageNotificationListener", "start");
                service.startMusic();
            } else if (intent.getAction().equals("music_notification_stop")) {
                Log.e("ImageNotificationListener", "stop");
                service.pauseMusic();
            }
        }
        else
        {
            myService.stopSelf();
        }
    }


}
