package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Model.Settings;

public class UpdatePolicyActivity extends AppCompatActivity {
    String previousActivity = "";
    String button = "";
    String policyID = "";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_policy);

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("previousActivity");
        button = intent.getStringExtra("button");
        policyID = intent.getStringExtra("policyID");

        JSONObject jsonObject = new JSONObject();

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

        try{
            jsonObject.put("tipo", settings.getString("type", ""));
            jsonObject.put("id", policyID);
            jsonObject.put("idAseguradora", insurerID);
        } catch (JSONException exception){
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

                    TextView policyIDTV = (TextView) findViewById(R.id.policyID);
                    policyIDTV.setText(policyID);

                    String[] arraySpinner1;
                    arraySpinner1 = new String[] {"Cobertura Amplia", "Cobertura Limitada"};
                    Spinner s1 = (Spinner) findViewById(R.id.policyType);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(UpdatePolicyActivity.this, android.R.layout.simple_spinner_item, arraySpinner1);
                    s1.setAdapter(adapter1);
                    String policyTypeString = responseJson.getString("tipo");
                    if(policyTypeString.equalsIgnoreCase("0"))
                    {
                        int spinnerPosition = adapter1.getPosition("Cobertura Amplia");
                        s1.setSelection(spinnerPosition);
                    }
                    else
                    {
                        int spinnerPosition = adapter1.getPosition("Cobertura Limitada");
                        s1.setSelection(spinnerPosition);
                    }

                    JSONObject automovil = (JSONObject)responseJson.get("automovil");

                    TextView vin = (TextView) findViewById(R.id.vin);
                    vin.setText(automovil.getString("vin"));

                    EditText plate = (EditText) findViewById(R.id.plate);
                    plate.setText(automovil.getString("placa"));

                    DatePicker startDate = (DatePicker) findViewById(R.id.startDate);
                    String startChaincodeTimestamp = responseJson.getString("fechaIni");
                    startChaincodeTimestamp = startChaincodeTimestamp.substring(0,startChaincodeTimestamp.indexOf('T'));
                    Log.d("App", "Substring: " + startChaincodeTimestamp);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date d1 = sdf.parse(startChaincodeTimestamp);
                        Calendar c = Calendar.getInstance();
                        c.setTime(d1);
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);
                        startDate.updateDate(year, month, day);
                    } catch (ParseException ex) {
                        Log.d("App", "Error en startDate");
                    }

                    DatePicker endDate = (DatePicker) findViewById(R.id.endDate);
                    String endChaincodeTimestamp = responseJson.getString("fechaFin");
                    endChaincodeTimestamp = endChaincodeTimestamp.substring(0,endChaincodeTimestamp.indexOf('T'));
                    Log.d("App", "Substring: " + endChaincodeTimestamp);
                    try {
                        Date d2 = sdf.parse(endChaincodeTimestamp);
                        Calendar c = Calendar.getInstance();
                        c.setTime(d2);
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);
                        endDate.updateDate(year, month, day);
                    } catch (ParseException ex) {
                        Log.d("App", "Error en endDate");
                    }

                    String[] arraySpinner2;
                    arraySpinner2 = new String[] {"Activa", "Deshabilitada"};
                    Spinner s2 = (Spinner) findViewById(R.id.status);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(UpdatePolicyActivity.this, android.R.layout.simple_spinner_item, arraySpinner2);
                    s2.setAdapter(adapter2);
                    Boolean policyStatusBoolean = (Boolean) responseJson.get("estatus");
                    Log.d("App", "Status: " + policyStatusBoolean.booleanValue());
                    if(policyStatusBoolean.booleanValue())
                    {
                        int spinnerPosition = adapter2.getPosition("Habilitada");
                        s2.setSelection(spinnerPosition);
                    }
                    else
                    {
                        int spinnerPosition = adapter2.getPosition("Deshabilitada");
                        s2.setSelection(spinnerPosition);
                    }

                    TextView owner = (TextView) findViewById(R.id.owner);
                    JSONObject ownerJson = (JSONObject) responseJson.get("cliente");
                    owner.setText(ownerJson.getString("nombre") + " " + ownerJson.getString("apellidoPaterno") + " " + ownerJson.getString("apellidoMaterno"));

                }catch (JSONException exception){
                    Log.d("App", exception.toString());
                }
                
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", "Error: " + error.toString());
                if(error.getClass() == AuthFailureError.class){
                    if(settings.getString("type", "").equalsIgnoreCase("aseguradora")){
                        Toast.makeText(UpdatePolicyActivity.this, "Usted no está autorizado para consultar esa póliza", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdatePolicyActivity.this, "Esa póliza no pertenece a la aseguradora indicada", Toast.LENGTH_SHORT).show();
                    }

                } else{
                    if(error.getClass() == TimeoutError.class){
                        Toast.makeText(UpdatePolicyActivity.this, "Póliza no encontrada", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(UpdatePolicyActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(UpdatePolicyActivity.this);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    public void updatePolicy (View view){
        Log.d("App", "Registro de póliza nueva...");

        TextView policyID = (TextView) findViewById(R.id.policyID);
        final String policyIDString = policyID.getText().toString();
        Spinner policyType = (Spinner)findViewById(R.id.policyType);
        String policyTypeString = policyType.getSelectedItem().toString();
        TextView vin = (TextView) findViewById(R.id.vin);
        String vinString = vin.getText().toString();
        EditText plate = (EditText)findViewById(R.id.plate);
        String plateString = plate.getText().toString();
        DatePicker startDate = (DatePicker) findViewById(R.id.startDate);
        String startDateString = startDate.getYear() +"-" + formatDate(startDate.getMonth()+1) + "-" + formatDate(startDate.getDayOfMonth()) + "T00:00:00Z";
        DatePicker endDate = (DatePicker) findViewById(R.id.endDate);
        String endDateString = endDate.getYear() +"-" + formatDate(endDate.getMonth()+1) + "-" + formatDate(endDate.getDayOfMonth()) + "T00:00:00Z";
        Spinner status = (Spinner)findViewById(R.id.status);
        String statusString = status.getSelectedItem().toString();
        TextView owner = (TextView) findViewById(R.id.owner);
        String ownerString = owner.getText().toString();

        String errorMessage = "";

        if(ownerString.equalsIgnoreCase("")){
            errorMessage = "Introduzca el nombre del titular";
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
        if(policyIDString.equalsIgnoreCase("")){
            errorMessage = "Introduzca el ID de la póliza";
        }

        if(errorMessage.equalsIgnoreCase("")){
            Log.d("App", "Datos de la póliza: " + policyIDString + ", " + policyTypeString + ", " + vinString + ", " + plateString + ", " + startDateString + ", " + endDateString + ", " + statusString + ", " + ownerString);
            Log.d("App", "Actualizando la póliza...");

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
                String apellidoPaterno = ownerString.substring(ownerString.indexOf(" "),ownerString.lastIndexOf(" "));
                String apellidoMaterno = ownerString.substring(ownerString.lastIndexOf(" "), ownerString.length());
                String nombre = ownerString.substring(0,ownerString.indexOf(" "));
                cliente.put("apellidoPaterno", apellidoPaterno);
                cliente.put("apellidoMaterno", apellidoMaterno);
                cliente.put("nombre", nombre);
                cliente.put("correo", "");
                cliente.put("telefono", "");
                Log.d("App", "Nombre: " + nombre + ", A. Paterno: " + apellidoPaterno + ", A. Materno: " + apellidoMaterno);
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

                jsonObject.put("id", policyIDString);

                params.put("change", jsonObject);
                params.put("tipo", settings.getString("type", ""));
                params.put("usuario", username);
            } catch (JSONException exception){
                Log.d("App", exception.toString());
            }

            Log.d("App", "Params: "+params.toString());
            String url = "http://" + appSettings.getIpAddress()+ "/changeInfoPoliza";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("App", "Response: " + response);
                    JSONObject responseJson = (JSONObject) response;

                    Intent intent = new Intent(UpdatePolicyActivity.this, ConfirmPolicyRegistrationActivity.class);
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
                    Toast.makeText(UpdatePolicyActivity.this, "Hubo un error al actualizar la póliza " + policyIDString, Toast.LENGTH_SHORT).show();
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

            RequestQueue requestQueue = Volley.newRequestQueue(UpdatePolicyActivity.this);
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonRequest);

        }else{
            Toast.makeText(UpdatePolicyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    public void back (View view){
        if (previousActivity.equalsIgnoreCase("InsurerMainActivity")){
            Intent intent = new Intent(UpdatePolicyActivity.this, InsurerMainActivity.class);
            intent.putExtra("previousActivity", "UpdatePolicyActivity");
            startActivity(intent);
        }else{

        }
    }
}
