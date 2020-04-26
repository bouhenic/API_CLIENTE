package com.example.api_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView text;
    TextView text2;
    TextView text3;
    RadioGroup radioGroup;
    EditText temp;
    EditText index;
    EditText addIp;
    String temperature;
    Button bouton;
    String strUrl;
    String typeRequest;
    String postData;
    String contentType;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.texto);
        text2 = (TextView)findViewById(R.id.texto2);
        text3 = (TextView) findViewById(R.id.texto3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupe);
        temp = (EditText) findViewById(R.id.editText);
        index =(EditText) findViewById(R.id.index);
        addIp =(EditText) findViewById(R.id.editIP);


        Context context = getApplicationContext();
        CharSequence text = "Requête envoyée";
        int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);



        bouton = (Button)findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                toast.show();
                bouton = findViewById(radioId);
                typeRequest= (String) bouton.getText();
                temperature="TEMP="+temp.getText();
                String json = "{\"temp\":\""+temp.getText()+"\"}";

                switch(typeRequest) {
                    case "POST" :
                        postData=temperature;
                        strUrl= "http://"+addIp.getText()+":8888/Test/API/temperature";
                        contentType="application/x-www-form-urlencoded";
                    break;

                    case "DELETE" :
                        postData= " ";
                        strUrl= "http://"+addIp.getText()+":8888/Test/API/temperature/"+index.getText();
                        contentType="application/x-www-form-urlencoded";
                     break;

                    case "PUT" :
                        postData= json;
                        strUrl= "http://"+addIp.getText()+":8888/Test/API/temperature/"+index.getText();
                        contentType="application/json";
                        break;

                    case "GET" :
                        postData=" ";
                        strUrl= "http://"+addIp.getText()+":8888/Test/API/temperature/1";
                        contentType="application/x-www-form-urlencoded";
                        break;
                }

                //text.setText(postData);
                new HTTPReqTask().execute(strUrl,typeRequest,postData,contentType);

            }
        });


    }

    private class HTTPReqTask extends AsyncTask<String, Void, String> {
        public HTTPReqTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String strUrl=params[0];
            String typeRequest = params[1];
            String postData = params[2];
            String contentType = params[3];
            StringBuffer buffer = null;


            try {
                switch (typeRequest) {

                    case "GET" :
                        URL url = new URL(strUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();
                    break;
                    default:
                        url = new URL(strUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Content-Type", contentType);
                        urlConnection.setRequestMethod(typeRequest);
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setChunkedStreamingMode(0);

                        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                out, "UTF-8"));
                        writer.write(postData.toString());
                        writer.flush();
                        break;
               }

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                buffer = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                   buffer.append(line);
                    //resultat = line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return buffer.toString();

        }

        @Override
        protected void onPostExecute(String buffer) {
            super.onPostExecute(buffer);
            switch(typeRequest) {
                case "GET" :
                String temp = null;
                String time = null;
                String id = null;
                JSONArray mainJsonArray = null;
                try {
                    mainJsonArray = new JSONArray(buffer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < mainJsonArray.length(); i++) {
                    JSONObject mainJsonObjet = null;
                    try {
                        mainJsonObjet = mainJsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        temp = mainJsonObjet.getString("TEMP");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        time = mainJsonObjet.getString("TIME");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        id=mainJsonObjet.getString("ID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                text.setText("température : "+temp +"°C");
                text2.setText("Date : "+time);
                text3.setText("index : "+id);
                break;
            }

            }

    }
}