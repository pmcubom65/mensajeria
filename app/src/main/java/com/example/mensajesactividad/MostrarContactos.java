package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.controladores.Presentacion;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.navigation.NavigationView;

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


public class MostrarContactos extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    String urlcrearusuario="http://192.168.1.37/api/crearusuario.php";

    RequestQueue requestQueue;
    String insertchat="http://192.168.1.37/api/crearchat.php";
    String showchat="http://192.168.1.37/api/chats_service.php";
    String buscarusuario="http://192.168.1.37/api/buscarusuario.php";

    public static String chat_id_empiece;
    public static String telefono_chat;
    String id;
    String inicio;

    private Usuario usuario;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_contactos);

        toolbar=findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
    //    getSupportActionBar().setLogo(R.drawable.smart_prod);
        getSupportActionBar().setTitle(null);

        drawerLayout=findViewById(R.id.midrawer);
        navigationView=findViewById(R.id.minavegacion);

        final ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamburguesa);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mihome:
                        Intent homeintent=new Intent(MostrarContactos.this, Presentacion.class);
                        drawerLayout.closeDrawers();
                        startActivity(homeintent);
                        return true;

                    case R.id.miexit:
                        System.exit(0);
                        return true;

                    case R.id.menuchat:
                        System.out.println("chats");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.perfilicono:
                        System.out.println("profile");
                        drawerLayout.closeDrawers();
                        return true;
                }

                return false;
            }
        });

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
                        id= String.valueOf(zdt.toInstant().toEpochMilli());

                        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        inicio=fechaactual.format(dtf);

                 //       guardarUsuario(contactos.get(position).getTelefono().toString().replaceAll("[\\D]", ""), contactos.get(position).getNombre().toString());

                        telefono_chat=contactos.get(position).getTelefono().toString().replaceAll("[\\D]", "");

                        buscarUsuario(telefono_chat);



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
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("chat_id", id);
       /* intent.putExtra("tokenaenviar", usuario.getToken().toString());
        intent.putExtra("tokenorigen", Autenticacion.tokenorigen);
        intent.putExtra("nombreemisor", Autenticacion.nombredelemisor);

        intent.putExtra("nombrereceptor", usuario.getNombre().toString());

        intent.putExtra("numerodetelefono", Autenticacion.numerotelefono);
        intent.putExtra("numerodetelefonoreceptor", usuario.getTelefono().toString());*/

       intent.putExtra("usuarioemisor", new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen));
       intent.putExtra("usuarioreceptor", usuario);


        startActivity(intent);
    }




    private void guardarUsuario(String telefono) {
        StringRequest request=new StringRequest(Request.Method.POST, urlcrearusuario, new Response.Listener<String>() {
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
                parameters.put("telefono", telefono);
                parameters.put("nombre", "guardar");
                return parameters;
            }
        };


        requestQueue.add(request);
    }





    private void buscarUsuario(String telefonobuscar) {

        System.out.println("buscando al usuario....");

        StringRequest request=new StringRequest(Request.Method.POST, buscarusuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsnobject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsnobject.getJSONArray("usuario");
                    if (jsonArray.length()==0) {
                        Toast.makeText(getBaseContext(), "El usuario no se encuentra registrado", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            usuario=new Usuario(explrObject.getString("telefono"), explrObject.getString("nombre"), null, explrObject.getString("token"));
                            crearChat(id, inicio);
                        }
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

                parameters.put("telefono", telefonobuscar);

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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}