package com.example.mensajesactividad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.modelos.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Perfil extends AppCompatActivity {

    TextView tnombre;
    TextView ttelefono;
    TextView tchats;
    String url="http://10.0.2.2/api/contarchats.php";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tnombre=findViewById(R.id.perfilnombre);
        ttelefono=findViewById(R.id.perfiltelefono);
        tchats=findViewById(R.id.perfilchats);

        tnombre.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ttelefono.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tchats.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        tnombre.setText("NOMBRE "+Autenticacion.nombredelemisor);
        ttelefono.setText("TELEFONO "+Autenticacion.numerotelefono);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        numeroDeChats();


    }


    public void numeroDeChats() {


            StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsnobject = new JSONObject(response.toString());
                        tchats.setText("Número de chats abiertos "+jsnobject.getString("chats_abiertos"));
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
                    parameters.put("telefono", Autenticacion.numerotelefono);

                    return parameters;
                }
            };

            requestQueue.add(request);



    }
}