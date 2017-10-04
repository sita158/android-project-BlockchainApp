package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Model.Settings;

public class MainActivity extends AppCompatActivity {
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        String username = settings.getString("username", "");
        Log.d("App", "Username in shared preferences: "+username);

        if(!username.equalsIgnoreCase("")){
            if(username.equalsIgnoreCase("AXXA") || username.equalsIgnoreCase("Mapfre")){
                Intent intent = new Intent(MainActivity.this, InsurerMainActivity.class);
                startActivity(intent);
            } else{
                if(username.equalsIgnoreCase("policia01") || username.equalsIgnoreCase("policia02")){
                    Intent intent = new Intent(MainActivity.this, AuthorityMainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    public void login (View view){
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        final String usernameString = username.getText().toString();
        final String passwordString = password.getText().toString();

        String errorString = "";

        if(passwordString.equalsIgnoreCase(""))
        {
            errorString="Ingresa tu password";
        }
        if(usernameString.equalsIgnoreCase(""))
        {
            errorString="Ingresa tu nombre de usuario";
        }

        if (errorString.equalsIgnoreCase("")){

            Log.d("App", "Usuario: " + usernameString + ", Contraseña: " + passwordString);
            Settings appSettings = Settings.getInstance();

            String url = "http://" + appSettings.getIpAddress()+ "/login";

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("user", usernameString);
                jsonObject.put("password", passwordString);
            } catch (JSONException exception){
                Log.d("App", exception.toString());
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("App", "Response: " + response);
                    JSONObject responseJson = (JSONObject)response;
                    String user = "";
                    String type = "";
                    try{
                        user = responseJson.getString("usuario");
                        type = responseJson.getString("tipo");
                    } catch (JSONException exception){
                        Log.d ("App", exception.toString());
                    }


                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", user);
                    editor.putString("type", type);
                    editor.commit();

                    if(type.equalsIgnoreCase("aseguradora")){
                        Intent intent = new Intent(MainActivity.this, InsurerMainActivity.class);
                        intent.putExtra("previousActivity", "MainActivity");
                        startActivity(intent);
                    } else{
                        if(type.equalsIgnoreCase("policia")){
                            Intent intent = new Intent(MainActivity.this, AuthorityMainActivity.class);
                            intent.putExtra("previousActivity", "MainActivity");
                            startActivity(intent);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("App", "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Combinación de usuario y contraseña no válidos", Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonRequest);

        } else {
            Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_SHORT).show();
        }

    }
}
