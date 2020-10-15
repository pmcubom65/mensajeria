package com.example.mensajesactividad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.RemoteInput;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

//http://lessons.livecode.com/m/4069/l/59312-how-do-i-use-push-notifications-with-android
//https://stackoverflow.com/questions/16386392/android-hello-world-pushnotification-example

public class MyBroadcastReceiver extends BroadcastReceiver {

    String KEY_REPLY = "key_reply";
    String urlcrearmensaje="http://10.0.2.2/api/crearmensaje.php";
    String id_mensaje;
    RequestQueue requestQueue;
    String chat_id;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("broadcast ");
        requestQueue= Volley.newRequestQueue(context.getApplicationContext());



        CharSequence contenido = getMessageText(intent);
        chat_id=intent.getStringExtra("chat_id");
       // String telefono=intent.getStringExtra("telefono");

       Usuario usuarioemisor=(Usuario) intent.getSerializableExtra("usuarioemisor");
       Usuario usuarioreceptor=(Usuario) intent.getSerializableExtra("usuarioreceptor");

        LocalDateTime ahora= LocalDateTime.now();
        ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dia=ahora.format(dtf);

        Mensaje mensaje=new Mensaje(contenido.toString(), dia, usuarioemisor.getTelefono().toString());
        id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());
        grabarMensaje(mensaje, id_mensaje);

    }



    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
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
                parameters.put("chat_id", chat_id);
                parameters.put("telefono", m.getTelefono());
                return parameters;
            }
        };

        requestQueue.add(request);
    }
}
