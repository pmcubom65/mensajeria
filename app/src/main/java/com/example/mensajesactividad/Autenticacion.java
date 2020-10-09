package com.example.mensajesactividad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Autenticacion extends AppCompatActivity {

    Button miboton;
    TextView textotelefono;
    SmsManager smsManager = SmsManager.getDefault();
    EditText textoaeditar;

    Button botonsms;
    private final int REQUEST_READ_PHONE_STATE=1;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);

        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_READ_PHONE_STATE);
        } else {
            getPermissionToReadSMS();
           seguir(tMgr.getLine1Number().toString());
        }

    }




    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS_REQUEST);

        } else {
            System.out.println("Permisisos de sms");

        }
    }



    public void seguir(String telefono) {

        textotelefono=findViewById(R.id.textotelefono);
        textotelefono.setText("Tu número de telefono es " +telefono);

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






}