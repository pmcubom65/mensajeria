package com.example.mensajesactividad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MostrarContactos extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;


    RequestQueue requestQueue;
    String insertchat="http://192.168.1.38/api/crearchat.php";
    String showchat="http://192.168.1.38/api/chats_service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_contactos);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Usuario> contactos = (ArrayList<Usuario>) args.getSerializable("ARRAYLIST");


        recyclerView=findViewById(R.id.miscontactos);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");

                        LocalDateTime fechaactual= LocalDateTime.now();
                        ZonedDateTime zdt = fechaactual.atZone(ZoneId.of("Europe/Andorra"));
                        String id= String.valueOf(zdt.toInstant().toEpochMilli());
                        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String inicio=fechaactual.format(dtf);
                        System.out.println(id);
                        System.out.println(inicio);
                        crearChat(id, inicio);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        myAdapter=new AdaptadorContactos(this, contactos);
        recyclerView.setAdapter(myAdapter);

    }


    public void crearChat(String id, String inicio){

        StringRequest request=new StringRequest(Request.Method.POST, insertchat, new Response.Listener<String>() {
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
                parameters.put("chat_id",id);
                parameters.put("inicio", inicio);
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}