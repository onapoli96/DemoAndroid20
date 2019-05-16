package com.example.orlando.demoandroid20;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MainActivity extends AppCompatActivity  {

    MqttHelper mqttHelper;
    TextView dataReceived;
    TextView segnaleReceived;
    EditText dataInviated;
    protected final String TAG = MainActivity.this.getClass().getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BeaconHelper beaconHelper;
    private Button startReadingBeaconsButton;
    private Button stopReadingBeaconsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataReceived = (TextView) findViewById(R.id.dataReceived);
        dataInviated = (EditText) findViewById(R.id.dataInviated);
        segnaleReceived = (TextView) findViewById(R.id.segnaleReceived);

        startMqtt();
        beaconHelper = new BeaconHelper(this, segnaleReceived);
        startReadingBeaconsButton = (Button) findViewById(R.id.startReadingBeaconsButton);
        stopReadingBeaconsButton = (Button) findViewById(R.id.stopReadingBeaconsButton);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForLocationPermissions();
            askForBluetooth();
        }

    }



    private boolean askForBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showToastMessage(getString(R.string.not_support_bluetooth_msg));
            return false;
        }
        else if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        //Controllo la risposta
        if(mBluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }



   @RequiresApi(api = Build.VERSION_CODES.M)
   private void askForLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_not_granted));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                    askForLocationPermissions();
                }
                return;
            }
        }
    }


    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconHelper.clear();
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void invia(View v) throws MqttException {
        MqttMessage message = new MqttMessage(dataInviated.getText().toString().getBytes());
        mqttHelper.publica(message);
    }

    public void inviaSegnale(View v) throws MqttException {
        MqttMessage message = new MqttMessage(segnaleReceived.getText().toString().getBytes());
        mqttHelper.publica(message);
    }

    public void startDetectingBaconButton(View v){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                askForLocationPermissions();

            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    beaconHelper.startDetectingBeacons();
                    changeStartButtonColors(stopReadingBeaconsButton,v);
                }
                else{
                    if(askForBluetooth()){
                        beaconHelper.startDetectingBeacons();
                        changeStartButtonColors(stopReadingBeaconsButton,v);
                    }
                }
            }

        } else {
           if (mBluetoothAdapter.isEnabled()) {
               beaconHelper.startDetectingBeacons();
               changeStartButtonColors(stopReadingBeaconsButton,v);
           }
           else{
               if(askForBluetooth()){
                   beaconHelper.startDetectingBeacons();
                   changeStartButtonColors(stopReadingBeaconsButton,v);
               }
           }
        }
    }

    private void changeStartButtonColors(View able, View disable){

        able.setEnabled(true);
        able.setAlpha(1);

        disable.setEnabled(false);
        disable.setAlpha(.5f);
    }

    public void stopDetectingBaconButton(View v){
        beaconHelper.stopDetectingBeacons();
        /*BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }*/

        changeStartButtonColors(startReadingBeaconsButton, v);
    }
}

