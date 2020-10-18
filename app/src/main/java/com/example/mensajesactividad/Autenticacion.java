package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Autenticacion extends AppCompatActivity  {

    Button botonempezar;

    EditText textoponertelefono;

    EditText ponernombre;
    RequestQueue requestQueue;


    private final int REQUEST_READ_PHONE_STATE=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int BIND_ACCESSIBILITY_SERVICE=1;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST=1;
    static String numerotelefono;
    String urlcrearusuario="http://192.168.1.37/api/crearusuario.php";
    static String nombredelemisor;
    static String tokenorigen;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);


        cargarPreferencias();



        textoponertelefono= findViewById(R.id.confirmar);
        ponernombre=findViewById(R.id.nombre);
        botonempezar=findViewById(R.id.botonempiece);



        requestQueue= Volley.newRequestQueue(getApplicationContext());

      //  getPermissionToSendSMS();



        botonempezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textoponertelefono.getText().toString().length()>0 && ponernombre.getText().toString().length()>0) {
                    numerotelefono=textoponertelefono.getText().toString();
                    nombredelemisor=ponernombre.getText().toString();


                    getTokenTelefono();






                //    smsToken();
                //    guardarUsuario(numerotelefono.replaceAll("[\\D]", ""), ponernombre.getText().toString());
                //    getContactPermission();
                }else {
                    String salida="Número o nombre no válido";
                    mostrarError(salida);
                }
            }
        });


    }


    private void getTokenTelefono() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        tokenorigen = task.getResult().getToken();
                        System.out.println(tokenorigen);

                        guardarUsuario(numerotelefono.replaceAll("[\\D]", ""), ponernombre.getText().toString(), tokenorigen.toString());

                        // Log and toast
                        //   String msg = getString(R.string.msg_token_fmt, token);
                        //  Log.d("TOKEN", msg);
                        //   Toast.makeText(Autenticacion.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarUsuario(String telefono, String nombre, String token) {
        StringRequest request=new StringRequest(Request.Method.POST, urlcrearusuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               if (response.length()==0) {
                   guardarPreferencias(telefono,nombre, token);
                   getContactPermission();


               }else {
                  Snackbar.make(findViewById(R.id.autenticacionlayout), response, Snackbar.LENGTH_LONG).show();
                
               }

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("volley error"+error);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters=new HashMap<>();
                parameters.put("telefono", telefono);
                parameters.put("nombre", nombre);
                parameters.put("token", token);
                return parameters;
            }
        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(request);
    }



  /*  public void getPermisosLeerTelefono() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_READ_PHONE_STATE);
        } else {
            String salida="Permisos Rechazados";
            mostrarError(salida);
        }
    }*/


    public void getContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);

        } else {
            getContactList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getPermissionToSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);

        }else {
            smsToken();
        }
    }



    private void getContactList() {
        ArrayList<Usuario> listacontactos=new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println( "Name: " + name);
                       System.out.println( "Name: " + phoneNo);

                        System.out.println( "my_contact_Uri: " + my_contact_Uri);

                        listacontactos.add(new Usuario(phoneNo, name, my_contact_Uri.toString()));

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        Set<Usuario> set = new HashSet<>(listacontactos);
        listacontactos.clear();
        listacontactos.addAll(set);


        Intent intent=new Intent(this, MostrarContactos.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void smsToken() {
        SmsManager smsManager = SmsManager.getDefault();

        String appSmsToken= smsManager.createAppSpecificSmsToken(createSmsTokenPendingIntent());

        smsManager.sendTextMessage(numerotelefono, null, "Hola!, autenticación correcta", null, null);
        smsManager.sendTextMessage(numerotelefono, null, appSmsToken, null, null);
    }


    private PendingIntent createSmsTokenPendingIntent() {
        return PendingIntent.getActivity(this, 1234, new Intent(this, MostrarContactos.class),0);
    }



   public void mostrarError(String error){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){

            getContactList();
        }  else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){

            smsToken();

        }else {
            String salida="Permisos Rechazados";
            mostrarError(salida);
        }
    }


    private void guardarPreferencias(String telefono, String nombre, String token){
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("telefono", telefono);
        editor.putString("token", token);
        editor.putString("nombre", nombre);
        editor.commit();

    }

    private void cargarPreferencias(){
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        String telefono=preferences.getString("telefono", "");
        String token=preferences.getString("token", "");
        String nombre=preferences.getString("nombre", "");

        if (telefono.length()>0 && token.length()>0 && nombre.length()>0){
            System.out.println("preferencias funcionan");
            numerotelefono=telefono;
            nombredelemisor=nombre;
            tokenorigen=token;
            getContactList();
        }

    }
}