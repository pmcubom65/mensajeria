package com.example.mensajesactividad;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {


     String tokenSMS= "d272bfe182b9e7d9d1666cdd754accb96421fcc25a8d17ff730af13d740b369b";
     String miurl="https://apps.netelip.com/sms/api.php";
     String mensajeaenviar;

     public GetUrlContentTask(String nombreRemitente, String destinatario, String texto) {

         mensajeaenviar="token=" + this.tokenSMS + "&from=" + nombreRemitente + "&destination=" + destinatario + "&message=" + texto;

     }

    protected void onProgressUpdate(Integer... progress) {
    }



    /*
    OkHttpClient client = new OkHttpClient();
    RequestBody formBody = new FormBody.Builder()
                      .add("email", emailString) // A sample POST field
                      .add("comment", commentString) // Another sample POST field
                      .build();
Request request = new Request.Builder()
                 .url("https://yourdomain.org/callback.php") // The URL to send the data to
                 .post(formBody)
                 .build();*/

    @Override
    protected String doInBackground(String... strings) {
         try {

             OkHttpClient client = new OkHttpClient();
             RequestBody formBody = new FormBody.Builder()
                     .add("email", emailString) // A sample POST field
                     .add("comment", commentString) // Another sample POST field
                     .build();



             URL url = new URL( this.miurl);
             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             System.out.println("aqui llega");
             connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

             connection.setRequestMethod("POST");

             connection.setDoOutput(false);
             connection.setConnectTimeout(5000);
             connection.setReadTimeout(5000);

             connection.connect();
             BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             String content = "", line;
             while ((line = rd.readLine()) != null) {
                 content += line + "\n";
             }

             return "SMS enviado con éxito";
         }catch (IOException e ){
             return "Error en el envío";
         }


    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        //displayMessage(result);
    }
}
