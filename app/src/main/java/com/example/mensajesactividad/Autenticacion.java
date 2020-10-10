package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;

public class Autenticacion extends AppCompatActivity  {

    Button botonempezar;

    EditText textoponertelefono;


    private final int REQUEST_READ_PHONE_STATE=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST=1;
    String numerotelefono;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);

        textoponertelefono= findViewById(R.id.confirmar);
        botonempezar=findViewById(R.id.botonempiece);

        getContactPermission();



  //    getPermissionToSendSMS();



        botonempezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textoponertelefono.getText().toString().length()>0) {
                    numerotelefono=textoponertelefono.getText().toString();
                    smsToken();
                }else {
                    String salida="Número no válido";
                    mostrarError(salida);
                }
            }
        });


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
        return PendingIntent.getActivity(this, 1234, new Intent(this, SmsTokenResultVerificationActivity.class),0);
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
}