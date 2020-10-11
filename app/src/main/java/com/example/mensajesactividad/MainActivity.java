package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> datosAmostrar;
    private FloatingActionButton botonenviar;
    private TextView textoenviar;

    private final String canal="5555";
    private final int notificationid=001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        final ArrayList<String> myDataset = new ArrayList<>()  ;


        mAdapter = new MyAdapter(this, myDataset);
        recyclerView.setAdapter(mAdapter);

        botonenviar=(FloatingActionButton) findViewById(R.id.botonmandarmensaje);
        textoenviar=(TextView) findViewById(R.id.textoanadir);

        botonenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataset.add(textoenviar.getText().toString());

                mAdapter.notifyDataSetChanged();
                notificationChannel();
                crearNotificacion();
            }
        });

    }

    public void crearNotificacion() {

        Intent siintent=new Intent(this, MainActivity.class);
        Intent nointent=new Intent(this, MainActivity.class);

       PendingIntent sipendingintent=PendingIntent.getActivity(this,0,siintent,PendingIntent.FLAG_ONE_SHOT);



        PendingIntent nopendingintent=PendingIntent.getActivity(this,0,nointent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notification=new NotificationCompat.Builder(this, canal);
        notification.setSmallIcon(R.drawable.round_rect_shape);
        notification.setContentTitle(textoenviar.getText().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(textoenviar.getText().toString()));
        notification.setContentText(textoenviar.getText().toString());
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setAutoCancel(true);
        //notification.setContentIntent(pendingIntent);

        notification.addAction(R.drawable.round_rect_shape, "Sí", sipendingintent);
        notification.addAction(R.drawable.round_rect_shape, "No", nopendingintent);


        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationid, notification.build());
    }


    public void notificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            String indicar= textoenviar.getText().toString();
            CharSequence personal=indicar;
            String descripcion=indicar;
            int importancia= NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel=new NotificationChannel(canal, personal, importancia);
            notificationChannel.setDescription(indicar);
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);



        }
    }


}


