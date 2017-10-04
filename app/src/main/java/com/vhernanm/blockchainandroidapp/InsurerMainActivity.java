package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InsurerMainActivity extends AppCompatActivity {
    SharedPreferences settings;
    String previousActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurer_main);

        Intent intent = getIntent();

        previousActivity = intent.getStringExtra("previousActivity");

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        String username = settings.getString("username", "");

        TextView usernameTV = (TextView)findViewById(R.id.username);
        usernameTV.setText("Bienvenido "+ username);
    }

    public void buttonPressed (View view){
        Button button = (Button) view;
        String buttonPressed = button.getText().toString();

        Log.d("App", "Botón: "+buttonPressed);

        if(buttonPressed.equalsIgnoreCase("Registro")){
            Intent intent = new Intent(InsurerMainActivity.this, RegisterNewPolicyActivity.class);
            intent.putExtra("previousActivity", "InsurerMainActivity");
            intent.putExtra("button", "Registro");
            startActivity(intent);
        }else{
            if(buttonPressed.equalsIgnoreCase("Actualización")){
                Intent intent = new Intent(InsurerMainActivity.this, FindPolicyActivity.class);
                intent.putExtra("previousActivity", "InsurerMainActivity");
                intent.putExtra("button", "Actualización");
                startActivity(intent);
            } else {
                if(buttonPressed.equalsIgnoreCase("Consulta")){
                    Intent intent = new Intent(InsurerMainActivity.this, FindPolicyActivity.class);
                    intent.putExtra("previousActivity", "InsurerMainActivity");
                    intent.putExtra("button", "Consulta");
                    startActivity(intent);
                }
            }
        }
    }

    public void logout (View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(InsurerMainActivity.this);
        builder.setTitle("Alerta...");
        builder.setMessage("¿Deseas cerrar sesión?");

        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.commit();
                Intent intent = new Intent(InsurerMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}
