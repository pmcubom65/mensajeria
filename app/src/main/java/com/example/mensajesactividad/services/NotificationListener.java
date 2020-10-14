package com.example.mensajesactividad.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {


    Context context;

    public NotificationListener() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        System.out.println("package "+sbn);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bundle extras = extras = sbn.getNotification().extras;
            String text = extras.getCharSequence("android.text").toString();
            String title = extras.getString("android.title");

            System.out.println("notificacion "+text+title);
        }


    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }


}
