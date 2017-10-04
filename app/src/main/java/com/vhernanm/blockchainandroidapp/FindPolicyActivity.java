package com.vhernanm.blockchainandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FindPolicyActivity extends AppCompatActivity {
    String previousActivity;
    String button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_policy);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("previousActivity");
        button = intent.getStringExtra("button");
        Log.d("App", "Actividad previa: " + previousActivity + ", botón: " + button);

        /*String[] arraySpinner1;
        arraySpinner1 = new String[] {"AXXA", "Mapfre"};
        Spinner s1 = (Spinner) findViewById(R.id.insurer);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner1);
        s1.setAdapter(adapter1);*/
    }

    public void findPolicy(View view){
        TextView policyID = (TextView) findViewById(R.id.policyID);
        String policyIDString = policyID.getText().toString();

        if(policyIDString.equalsIgnoreCase("")){
            Toast.makeText(FindPolicyActivity.this, "Ingrese ID de póliza", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("App", "Buscando póliza " + policyIDString);
            if(button.equalsIgnoreCase("Actualización")){
                Intent intent = new Intent(FindPolicyActivity.this, UpdatePolicyActivity.class);
                intent.putExtra("previousActivity", previousActivity);
                intent.putExtra("button", button);
                intent.putExtra("policyID", policyIDString);
                startActivity(intent);
            }else{
                if (button.equalsIgnoreCase("Consulta") || button.equalsIgnoreCase("ID Póliza")){
                    Intent intent = new Intent(FindPolicyActivity.this, ConfirmPolicyRegistrationActivity.class);
                    intent.putExtra("previousActivity", previousActivity);
                    intent.putExtra("button", button);
                    intent.putExtra("policyID", policyIDString);
                    startActivity(intent);
                }
            }
        }
    }

    public void back (View view){
        if(previousActivity.equalsIgnoreCase("InsurerMainActivity")){
            Intent intent = new Intent(FindPolicyActivity.this, InsurerMainActivity.class);
            intent.putExtra("previousActivity", "FindPolicyActivity");
            startActivity(intent);
        } else {
            if(previousActivity.equalsIgnoreCase("AuthorityMainActivity")){
                Intent intent = new Intent(FindPolicyActivity.this, AuthorityMainActivity.class);
                intent.putExtra("previousActivity", "FindPolicyActivity");
                startActivity(intent);
            }
        }
    }
}
