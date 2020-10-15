package com.example.mensajesactividad.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.mensajesactividad.Autenticacion;
import com.example.mensajesactividad.MainActivity;
import com.example.mensajesactividad.MyBroadcastReceiver;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.Usuario;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;
//https://stackoverflow.com/questions/37358462/firebase-onmessagereceived-not-called-when-app-in-background
public class MyFirebaseInstanceService extends FirebaseMessagingService {


    private final String canal="5555";
    private final int notificationid=001;
    String KEY_REPLY = "key_reply";

    String chat_id;
    String titulo;

    Usuario emisor;
    Usuario receptor;



    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TOKEN", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
      //  sendRegistrationToServer(token);
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       System.out.println( "From: " + remoteMessage.toString());

        Map<String, String> data = remoteMessage.getData();
        chat_id=(String) data.get("michatid");
        titulo=(String) data.get("titulo");


        String tokenemisor=(String) data.get("tokenaenviar");
        String nombreemisor=(String) data.get("nombrereceptor");
        String telefonoemisor=(String) data.get("telefonoreceptor");

        emisor=new Usuario(telefonoemisor, nombreemisor, null, tokenemisor);

        String tokenreceptor=(String) data.get("tokenemisor");
        String nombrereceptor=(String) data.get("nombreemisor");
        String telefonoreceptor=(String) data.get("telefonoemisor");

        receptor=new Usuario(telefonoreceptor, nombrereceptor, null, tokenreceptor);


        System.out.println(data);

        notificationChannel();
        crearNotificacion();
    }








    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void crearNotificacion() {

        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(), canal);
        notification.setSmallIcon(R.drawable.smartlabs);
        notification.setContentTitle(receptor.getNombre().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(titulo));
        notification.setContentText(titulo);
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setAutoCancel(true);

        String replyLabel = "Respuesta: ";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();


        Intent resultIntent = new Intent(this, MyBroadcastReceiver.class);
        resultIntent.putExtra("chat_id", chat_id);
        resultIntent.putExtra("usuarioemisor", emisor);
        resultIntent.putExtra("usuarioreceptor", receptor);


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);




        //Notification Action with RemoteInput instance added.
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "RESPONDER", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        //Notification.Action instance added to Notification Builder.
        notification.addAction(replyAction);


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notificationId", notificationid);
        intent.putExtra("chat_id", chat_id);
        intent.putExtra("usuarioemisor", emisor);
        intent.putExtra("usuarioreceptor", receptor);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ver mensaje", dismissIntent);


        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(notificationid, notification.build());
    }




    public void notificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            String indicar= "mas cosas";
            CharSequence personal=titulo;
            String descripcion=titulo;
            int importancia= NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel=new NotificationChannel(canal, personal, importancia);
            notificationChannel.setDescription(indicar);
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);



        }

    }








    @Override
    public void onCreate() {
        System.out.println("notificación está aquí");


    }
}
