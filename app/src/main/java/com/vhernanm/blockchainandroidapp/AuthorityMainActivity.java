package com.vhernanm.blockchainandroidapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;;

public class AuthorityMainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    SharedPreferences settings;
    String previousActivity = "";
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_main);
        Intent intent = getIntent();

        previousActivity = intent.getStringExtra("previousActivity");

        settings = getSharedPreferences("BlockchainAndroidApp", Context.MODE_PRIVATE);

        String username = settings.getString("username", "");

        TextView usernameTV = (TextView)findViewById(R.id.username);
        usernameTV.setText("Bienvenido "+ username);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
    }

    public void buttonPressed (View view){
        Button button = (Button) view;
        String buttonPressed = button.getText().toString();

        Log.d("App", "Botón: "+buttonPressed);

        if(buttonPressed.equalsIgnoreCase("Código QR")){
            qrScanner();
        }else{
            if(buttonPressed.equalsIgnoreCase("ID Póliza")){
                Intent intent = new Intent(AuthorityMainActivity.this, FindPolicyActivity.class);
                intent.putExtra("previousActivity", "AuthorityMainActivity");
                intent.putExtra("button", "ID Póliza");
                startActivity(intent);
            }
        }
    }

    public void logout (View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthorityMainActivity.this);
        builder.setTitle("Alerta...");
        builder.setMessage("¿Deseas cerrar sesión?");

        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.commit();
                Intent intent = new Intent(AuthorityMainActivity.this, MainActivity.class);
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

    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d("App", rawResult.getText()); // Prints scan results
        Log.d("App", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
       // show the scanner result into dialog box.<br />
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();*/
        Intent intent = new Intent(AuthorityMainActivity.this, ConfirmPolicyRegistrationActivity.class);
        intent.putExtra("previousActivity", "AuthorityMainActivity");
        intent.putExtra("button", "Código QR");
        //intent.putExtra("policyID", rawResult.getText());
        intent.putExtra("policyID", "1");
        startActivity(intent);
        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

    public void qrScanner(){
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
            super.onPause();
            mScannerView.stopCamera();   // Stop camera on pause
    }
}
