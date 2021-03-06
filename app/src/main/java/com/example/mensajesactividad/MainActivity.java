package com.example.mensajesactividad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;


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

// https://stackoverflow.com/questions/22252065/refreshing-activity-on-receiving-gcm-push-notification

public class MainActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Mensaje> datosAmostrar;
    private FloatingActionButton botonenviar;
    private TextView textoenviar;

    private final String canal="5555";
    private final int notificationid=001;
    String KEY_REPLY = "key_reply";
    public static int datos;

    String urlcrearmensaje="http://10.0.2.2/api/crearmensaje.php";

    String urlcargarmensajeschat="http://10.0.2.2/api/mostrarmensajeschat.php";

    String url="https://fcm.googleapis.com/fcm/send";

    RequestQueue requestQueue;
    String michatid;
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    Mensaje mensaje;

 /*   String tokenaenviarlosmensajes;
    String tokenemisor;
    String nombreemisor;
    String nombrereceptor;

    String telefonoreceptor;*/
    Usuario usuarioemisor;
    Usuario usuarioreceptor;
    private Toolbar toolbar;


    private BroadcastReceiver onMessage= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("cambio el recycler");
            cargarMensajesChat();
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Intent llegada=getIntent();
        michatid=(String) llegada.getExtras().get("chat_id");

   /*     tokenaenviarlosmensajes= (String) llegada.getExtras().get("tokenaenviar");
        tokenemisor=(String) llegada.getExtras().get("tokenorigen");
        nombreemisor=(String) llegada.getExtras().get("nombreemisor");
        nombrereceptor=(String) llegada.getExtras().get("nombrereceptor");
        telefonoreceptor=(String) llegada.getExtras().get("numerodetelefonoreceptor");*/

        usuarioemisor=(Usuario) llegada.getSerializableExtra("usuarioemisor");
        usuarioreceptor=(Usuario) llegada.getSerializableExtra("usuarioreceptor");





        requestQueue= Volley.newRequestQueue(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        toolbar=findViewById(R.id.mitoolbarmensajes);
        toolbar.setTitle("Conversando con "+usuarioreceptor.getNombre());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

                DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dia=ahora.format(dtf);

                System.out.println("voy a enviar un mensaje "+usuarioemisor);

                mensaje=new Mensaje(textoenviar.getText().toString(), dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());
                datosAmostrar.add(datosAmostrar.size(), mensaje);
                mAdapter.notifyItemChanged(datosAmostrar.size());
                String id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());
                grabarMensaje(mensaje, id_mensaje);
                cargarMensajesChat();

                notificationFirebase();
           //     notificationChannel();
          //      crearNotificacion();
            }
        });


        IntentFilter intentFilter= new IntentFilter("com.myApp.CUSTOM_EVENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessage, intentFilter);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void crearNotificacion() {

        NotificationCompat.Builder notification=new NotificationCompat.Builder(this, canal);
        notification.setSmallIcon(R.drawable.smartlabs);
        notification.setContentTitle(textoenviar.getText().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(textoenviar.getText().toString()));
        notification.setContentText(textoenviar.getText().toString());
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setAutoCancel(true);

        String replyLabel = "Respuesta: ";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        // Build a PendingIntent for the reply action to trigger.

        Intent resultIntent = new Intent(this, MyBroadcastReceiver.class);
        resultIntent.putExtra("chat_id", michatid);
        resultIntent.putExtra("telefono", usuarioreceptor.getTelefono().toString());

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
        intent.putExtra("chat_id", michatid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Rechazar", dismissIntent);

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

                try {
                    JSONObject jsnobject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsnobject.getJSONArray("mensajes_del_chat");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        Mensaje m=new Mensaje(explrObject.getString("contenido"), explrObject.getString("dia"), explrObject.getString("telefono"), explrObject.getString("nombre"));
                        System.out.println(m);

                        if (!datosAmostrar.contains(m)) {
                            datosAmostrar.add(m);
                        }


                    }
                    mAdapter.notifyDataSetChanged();
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




    public void notificationFirebase()   {

        //https://stackoverflow.com/questions/37731275/display-specific-activity-when-firebase-notification-is-tapped/38195369


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, michatid);
   //     bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "String");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        //our json object
        JSONObject mainObj=new JSONObject();
        String token="";
        try {


      //      String tokensegundo="dbMjzbyeRvK7H7X0JPFRml:APA91bGnxgY1r1waKY2Knmbc5kiSjtK12Z_IJkDmjKsJ7YuDvSN5w6phiWoIhGbTeEMOx89_78FUbluUr9CxMdb-vhnpp61IYwaJipBh4m0O66n0SSDlKl4hQT57uhdllhmL6rJacmFB";
     //       mainObj.put("to", tokenaenviarlosmensajes);

            mainObj.put("to", usuarioreceptor.getToken().toString());
            JSONObject notificationObj=new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("michatid", michatid);
            jData.put("titulo", mensaje.getContenido());

            jData.put("tokenaenviar", usuarioreceptor.getToken().toString());
            jData.put("tokenemisor", usuarioemisor.getToken().toString());


            jData.put("nombreemisor", usuarioemisor.getNombre().toString());
            jData.put("nombrereceptor", usuarioreceptor.getNombre().toString());


            jData.put("telefonoemisor", usuarioemisor.getTelefono().toString());
            jData.put("telefonoreceptor", usuarioreceptor.getTelefono().toString());

     /*       notificationObj.put("title", mensaje.getContenido());

            notificationObj.put("body", mensaje.getContenido());
            notificationObj.put("sound", "default");
            notificationObj.put("click_action", "CLICK_ACTION");*/


     //       mainObj.put("notification", notificationObj);
            mainObj.put("priority","high");

            mainObj.put("data", jData);
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    System.out.println("Notificación enviada");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                        System.out.println("Notificación erronea");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header=new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAafa8PTg:APA91bEafAQa2vygzlPALqd72Dik0BflDS7b-hCraAwZvzAkK-hLHsohWvsN1C5kHSSym3pdZx5M63COhYBPosP7Icu-JDXguENKkH3fvXco4CXroInSeLadlujJKpUrqoROt1ttGiW0");
                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }



}


