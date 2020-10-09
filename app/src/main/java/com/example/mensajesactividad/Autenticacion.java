package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Autenticacion extends AppCompatActivity {

    Button miboton;
    TextView textotelefono;
    SmsManager smsManager = SmsManager.getDefault();
    EditText textoaeditar;
    TelephonyManager tMgr;

    Button botonsms;
    private final int REQUEST_READ_PHONE_STATE=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);

        // Permisos para leer el número de telefono
        getPermisosLeerTelefono();


        // Permisos para leer la agenda de contactos
        getContactPermission();


    /*    IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)*/


        // Recuperamos contactos en un ArrayList
        ArrayList<Usuario> listadocontactos=getContactList();


        // Iniciamos intent para mostrar los contactos
        Intent intent = new Intent(this, MostrarContactos.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable) listadocontactos);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);



   /*    tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_READ_PHONE_STATE);
        } else {
            getPermissionToReadSMS();

        }*/

    }



    public void getPermisosLeerTelefono() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_READ_PHONE_STATE);
        } else {

            tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            seguir(tMgr.getLine1Number().toString());
            //   getPermissionToReadSMS();

        }
    }


    public void getContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);

        } else {
            System.out.println("Permisisos de sms");
         //   seguir(tMgr.getLine1Number().toString());

        }
    }




    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS_REQUEST);

        } else {
            System.out.println("Permisisos de sms");
            seguir(tMgr.getLine1Number().toString());

        }
    }



    public void seguir(String telefono) {

        textotelefono=findViewById(R.id.textotelefono);
        textotelefono.setText("Tu número de telefono es " +telefono);
        smsRetriever(telefono);

        Context c= this;

        textoaeditar =(EditText) findViewById(R.id.textoanadir);

        miboton=(Button) findViewById(R.id.botonempiece);

        botonsms= (Button) findViewById(R.id.botonenviarsms);

        botonsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(c, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    getPermissionToReadSMS();
                }else {
                    smsManager.sendTextMessage(textoaeditar.getText().toString(), null, "mensaje sms", null, null);
                }

            }
        });




        miboton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Autenticacion.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }



    private ArrayList<Usuario> getContactList() {
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
                   //     System.out.println( "Name: " + name);
                    //    System.out.println( "Name: " + phoneNo);



                        listacontactos.add(new Usuario(phoneNo, name));

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return listacontactos;
    }

   public void smsRetriever(String telefono) {
   //    SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
       SmsRetriever.getClient(this).startSmsUserConsent(telefono);

      /* Task<Void> task = client.startSmsRetriever();
       task.addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               // Successfully started retriever, expect broadcast intent
               // ...
           }
       });

       task.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               // Failed to start retriever, inspect Exception for more details
               // ...
           }
       });*/
    }
}