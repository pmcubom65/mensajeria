package com.example.mensajesactividad;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MySMSBroadcastReceiver extends BroadcastReceiver {
//https://blog.mindorks.com/easy-sms-verification-in-android-sms-user-consent-api
    // https://stackoverflow.com/questions/60351109/broadcast-receiver-is-not-receiving-sms-from-google-sms-retriever-api
    // https://stackoverflow.com/questions/58726014/how-to-automatically-read-sms-in-android

    private int SMS_CONSENT_REQUEST = 2;
    Activity activity;


    MySMSBroadcastReceiver(Activity activity){
        this.activity=activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    Intent consentIntent = (Intent) extras.get(SmsRetriever.EXTRA_CONSENT_INTENT);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.

                    activity.startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }



}
