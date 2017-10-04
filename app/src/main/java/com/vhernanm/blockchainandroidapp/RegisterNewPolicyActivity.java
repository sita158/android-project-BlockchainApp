package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Model.Settings;

public class RegisterNewPolicyActivity extends AppCompatActivity {
    String previousActivity = "";
    String button = "";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_policy);

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("previousActivity");
        button = intent.getStringExtra("button");

        String[] arraySpinner1;
        arraySpinner1 = new String[] {"Cobertura Amplia", "Cobertura Limitada"};
        Spinner s1 = (Spinner) findViewById(R.id.policyType);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner1);
        s1.setAdapter(adapter1);

        String[] arraySpinner2;
        arraySpinner2 = new String[] {"Activa", "Deshabilitada"};
        Spinner s2 = (Spinner) findViewById(R.id.status);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner2);
        s2.setAdapter(adapter2);
    }

    public void registerNewPolicy(View view){
        Log.d("App", "Registro de póliza nueva...");

        //EditText policyID = (EditText)findViewById(R.id.policyID);
        //String policyIDString = policyID.getText().toString();
        Spinner policyType = (Spinner)findViewById(R.id.policyType);
        final String policyTypeString = policyType.getSelectedItem().toString();
        final EditText vin = (EditText)findViewById(R.id.vin);
        final String vinString = vin.getText().toString();
        final EditText plate = (EditText)findViewById(R.id.plate);
        final String plateString = plate.getText().toString();
        DatePicker startDate = (DatePicker) findViewById(R.id.startDate);
        final String startDateString = startDate.getYear() +"-" + formatDate(startDate.getMonth()+1) + "-" + formatDate(startDate.getDayOfMonth()) + "T00:00:00Z";
        DatePicker endDate = (DatePicker) findViewById(R.id.endDate);
        final String endDateString = endDate.getYear() +"-" + formatDate(endDate.getMonth()+1) + "-" + formatDate(endDate.getDayOfMonth()) + "T00:00:00Z";
        Spinner status = (Spinner)findViewById(R.id.status);
        final String statusString = status.getSelectedItem().toString();
        EditText ownerName = (EditText)findViewById(R.id.ownerName);
        String ownerNameString = ownerName.getText().toString();
        EditText ownerLastname1 = (EditText)findViewById(R.id.ownerLastname1);
        final String ownerLastname1String = ownerLastname1.getText().toString();
        EditText ownerLastname2 = (EditText)findViewById(R.id.ownerLastname2);
        final String ownerLastname2String = ownerLastname2.getText().toString();

        String errorMessage = "";

        if(ownerNameString.equalsIgnoreCase("") || ownerLastname1String.equalsIgnoreCase("") || ownerLastname2String.equalsIgnoreCase("")){
            errorMessage = "Introduzca el nombre completo del titular";
        }
        if(statusString.equalsIgnoreCase("")){
            errorMessage = "Elija un estatus";
        }
        if(endDateString.equalsIgnoreCase("")){
            errorMessage = "Seleccione una fecha de finalización";
        }
        if(startDateString.equalsIgnoreCase("")){
            errorMessage = "Seleccione una fecha de inicio";
        }
        if(plateString.equalsIgnoreCase("")){
            errorMessage = "Introduzca la placa";
        }
        if(vinString.equalsIgnoreCase("")){
            errorMessage = "Introduzca el VIN";
        }
        if(policyTypeString.equalsIgnoreCase("")){
            errorMessage = "Seleccione un tipo de cobertura";
        }
        /*if(policyIDString.equalsIgnoreCase("")){
            errorMessage = "Introduzca el ID de la póliza";
        }*/

        if(errorMessage.equalsIgnoreCase("")){
            Log.d("App", "Datos de la póliza: " + policyTypeString + ", " + vinString + ", " + plateString + ", " + startDateString + ", " + endDateString + ", " + statusString + ", " + ownerNameString);
            Log.d("App", "Guardando la nueva póliza...");

            Settings appSettings = Settings.getInstance();

            JSONObject params = new JSONObject();

            JSONObject jsonObject = new JSONObject();

            JSONObject aseguradora = new JSONObject();

            try{
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
                aseguradora.put("idAseguradora", insurerID);
                aseguradora.put("nombre", username);
                jsonObject.put("aseguradora", aseguradora);

                JSONObject automovil = new JSONObject();
                automovil.put("placa", plateString);
                automovil.put("vin", vinString);
                jsonObject.put("automovil", automovil);

                JSONObject cliente = new JSONObject();
                cliente.put("apellidoPaterno", ownerLastname1String);
                cliente.put("apellidoMaterno", ownerLastname2String);
                cliente.put("nombre", ownerNameString);
                cliente.put("correo", "");
                cliente.put("telefono", "");
                jsonObject.put("cliente", cliente);

                jsonObject.put("fechaFin", endDateString);
                jsonObject.put("fechaIni", startDateString);

                if(policyTypeString.equalsIgnoreCase("cobertura amplia"))
                    jsonObject.put("tipo", 0);
                else
                    jsonObject.put("tipo", 1);

                if(statusString.equalsIgnoreCase("activa"))
                    jsonObject.put("estatus", true);
                else
                    jsonObject.put("estatus", false);

                params.put("create", jsonObject);
                params.put("tipo", settings.getString("type", ""));
            } catch (JSONException exception){
                Log.d("App", exception.toString());
            }

            Log.d("App", "Params: "+params.toString());
            String url = "http://" + appSettings.getIpAddress()+ "/createPoliza";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("App", "Response: " + response);
                    JSONObject responseJson = (JSONObject) response;
                    Intent intent = new Intent(RegisterNewPolicyActivity.this, ConfirmPolicyRegistrationActivity.class);
                    intent.putExtra("previousActivity", previousActivity);
                    intent.putExtra("button", button);
                    try{
                        intent.putExtra("policyID", responseJson.getString("id"));
                    } catch (JSONException exception){
                        Log.d("App", exception.toString());
                    }

                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("App", "Error: " + error.getMessage());
                    Toast.makeText(RegisterNewPolicyActivity.this, "Hubo un error al guardar la póliza.", Toast.LENGTH_SHORT).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-type", "application/json");

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(RegisterNewPolicyActivity.this);
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonRequest);

        }else{
            Toast.makeText(RegisterNewPolicyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }

    }

    private String formatDate (int x){
        String newFormat = "";
        if(x<10)
        {
            newFormat= "0"+x;
        }
        else{
            newFormat=""+x;
        }
        return newFormat;
    }

    public void back(View view){
        if (previousActivity.equalsIgnoreCase("InsurerMainActivity")){
            Intent intent = new Intent(RegisterNewPolicyActivity.this, InsurerMainActivity.class);
            intent.putExtra("previousActivity", "RegisterNewPolicyActivity");
            startActivity(intent);
        }

        /*if (previousActivity.equalsIgnoreCase("AuthorityMainActivity")){
            Intent intent = new Intent(RegisterNewPolicyActivity.this, AuthorityMainActivity.class);
            intent.putExtra("previousActivity", "RegisterNewPolicyActivity");
            startActivity(intent);
        }*/
    }
}
