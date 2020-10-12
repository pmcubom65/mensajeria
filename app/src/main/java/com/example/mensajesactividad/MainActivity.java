package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Mensaje> datosAmostrar;
    private FloatingActionButton botonenviar;
    private TextView textoenviar;

    private final String canal="5555";
    private final int notificationid=001;

    String urlcrearmensaje="http://192.168.1.39/api/crearmensaje.php";

    String urlcargarmensajeschat="http://192.168.1.39/api/mostrarmensajeschat.php";

    RequestQueue requestQueue;
    String michatid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent llegada=getIntent();
        michatid=(String) llegada.getExtras().get("chat_id");

        System.out.println("chatid creado "+michatid);

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        datosAmostrar = new ArrayList<>();

        cargarMensajesChat();


        mAdapter = new MyAdapter(this, datosAmostrar);
        recyclerView.setAdapter(mAdapter);

        requestQueue= Volley.newRequestQueue(getApplicationContext());

        botonenviar=(FloatingActionButton) findViewById(R.id.botonmandarmensaje);
        textoenviar=(TextView) findViewById(R.id.textoanadir);

        botonenviar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                LocalDateTime ahora= LocalDateTime.now();
                ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Andorra"));

                DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dia=ahora.format(dtf);

                Mensaje mensaje=new Mensaje(textoenviar.getText().toString(), dia, Autenticacion.numerotelefono);
                String id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());
                grabarMensaje(mensaje, id_mensaje);
                cargarMensajesChat();


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
        notification.setSmallIcon(R.drawable.smartlabs);
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


    public void grabarMensaje(Mensaje m, String id){

        StringRequest request=new StringRequest(Request.Method.POST, urlcrearmensaje, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters=new HashMap<>();
                parameters.put("mensaje_id",id);
                parameters.put("contenido", m.getContenido());
                parameters.put("dia", m.getFecha());
                parameters.put("chat_id", michatid);
                parameters.put("telefono", m.getTelefono());
                return parameters;
            }
        };

        requestQueue.add(request);
    }





    public void  cargarMensajesChat() {


        StringRequest request=new StringRequest(Request.Method.POST, urlcargarmensajeschat, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             //   System.out.println("respuesta recibida");
                 //   System.out.println(response.toString());
              //  Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsnobject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsnobject.getJSONArray("mensajes_del_chat");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        Mensaje m=new Mensaje(explrObject.getString("contenido"), explrObject.getString("dia"), explrObject.getString("telefono"));
                        System.out.println(m);

                        datosAmostrar.add(m);
                        mAdapter.notifyItemChanged(i, m);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters=new HashMap<>();
                System.out.println("busco este chat "+michatid);
                parameters.put("chat_id", michatid);

                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("Content-Type","application/x-www-form-urlencoded");
                return param;
            }
        };

        requestQueue.add(request);
    }


}


