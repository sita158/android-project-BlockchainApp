package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Settings;

public class ConfirmPolicyRegistrationActivity extends AppCompatActivity {
    String previousActivity = "";
    String button = "";
    String policyIDString = "";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_policy_registration);

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("previousActivity");
        button = intent.getStringExtra("button");
        policyIDString = intent.getStringExtra("policyID");

        Log.d("App", "Policy ID: " + policyIDString);

        final TextView policyID = (TextView) findViewById(R.id.policyID);
        final TextView policyType = (TextView) findViewById(R.id.policyType);
        final TextView vin = (TextView) findViewById(R.id.vin);
        final TextView plate = (TextView) findViewById(R.id.plate);
        final TextView startDate = (TextView) findViewById(R.id.startDate);
        final TextView endDate = (TextView) findViewById(R.id.endDate);
        final TextView status = (TextView) findViewById(R.id.status);
        final TextView owner = (TextView) findViewById(R.id.owner);
        final TextView confirmationMessage = (TextView) findViewById(R.id.confirmationMessage);

        if(button.equalsIgnoreCase("Consulta") || button.equalsIgnoreCase("ID Póliza")|| button.equalsIgnoreCase("Código QR")){
            confirmationMessage.setVisibility(View.INVISIBLE);
        }

        String username = settings.getString("username", "");
        String insurerID="A000";
        if(!username.equalsIgnoreCase("")){
            switch(username){
                case "AXXA":
                    insurerID="A001";
                    break;
                case "Mapfre":
                    insurerID="A002";
                    break;
            }
        }

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("tipo", settings.getString("type", ""));
            jsonObject.put("id", policyIDString);
            jsonObject.put("idAseguradora", insurerID);
        }catch (JSONException exception){
            Log.d("App", exception.toString());
        }


        Settings appSettings = Settings.getInstance();
        String url = "http://" + appSettings.getIpAddress()+ "/polizaPorId";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("App", "Response: " + response);
                try{
                    JSONObject responseJson = (JSONObject) response;
                    policyID.setText(policyIDString);
                    String policyTypeString = responseJson.getString("tipo");
                    if(policyTypeString.equalsIgnoreCase("0"))
                    {
                        policyType.setText("Cobertura amplia");
                    }
                    else
                    {
                        policyType.setText("Cobertura limitada");
                    }
                    JSONObject automovil = (JSONObject)responseJson.get("automovil");
                    vin.setText(automovil.getString("vin"));
                    plate.setText(automovil.getString("placa"));
                    startDate.setText(responseJson.getString("fechaIni"));
                    endDate.setText(responseJson.getString("fechaFin"));
                    Boolean policyStatus = (Boolean)responseJson.get("estatus");
                    if(policyStatus)
                    {
                        status.setText("Habilitada");
                    }
                    else
                    {
                        status.setText("Deshabilitada");
                    }
                    JSONObject ownerJson = (JSONObject)responseJson.get("cliente");
                    owner.setText(ownerJson.getString("nombre") + " " + ownerJson.getString("apellidoPaterno") + " " + ownerJson.getString("apellidoMaterno"));
                }catch (JSONException exception){
                    Log.d("App", exception.toString());
                }

                confirmationMessage.setText("La póliza " + policyIDString +" se ha registrado correctamente.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", "Error: " + error.toString());
                if(error.getClass() == AuthFailureError.class){
                    if(settings.getString("type", "").equalsIgnoreCase("aseguradora")){
                        Toast.makeText(ConfirmPolicyRegistrationActivity.this, "Usted no está autorizado para consultar esa póliza", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConfirmPolicyRegistrationActivity.this, "Esa póliza no pertenece a la aseguradora indicada", Toast.LENGTH_SHORT).show();
                    }

                } else{
                    if(error.getClass() == TimeoutError.class){
                        Toast.makeText(ConfirmPolicyRegistrationActivity.this, "Póliza no encontrada", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(ConfirmPolicyRegistrationActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ConfirmPolicyRegistrationActivity.this);
        //jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    public void back (View view){
        if(previousActivity.equalsIgnoreCase("InsurerMainActivity")){
            Intent intent = new Intent(ConfirmPolicyRegistrationActivity.this, InsurerMainActivity.class);
            intent.putExtra("previousActivity", "ConfirmPolicyRegistrationActivity");
            startActivity(intent);
        }else{
            if(previousActivity.equalsIgnoreCase("AuthorityMainActivity")){
                Intent intent = new Intent(ConfirmPolicyRegistrationActivity.this, AuthorityMainActivity.class);
                intent.putExtra("previousActivity", "ConfirmPolicyRegistrationActivity");
                startActivity(intent);
            }
        }
    }
}
