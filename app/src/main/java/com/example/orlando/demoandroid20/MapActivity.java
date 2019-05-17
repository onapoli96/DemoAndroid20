package com.example.orlando.demoandroid20;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.RangeNotifier;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements TextWatcher {

    private BeaconHelper beaconHelper;
    private Button startReadingBeaconsButton;
    private Button stopReadingBeaconsButton;
    private RangeNotifier rangeNotifier;
    private TextView hiddenTextView;
    private Button[] buttons;
    MqttHelper mqttHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        hiddenTextView = (TextView) findViewById(R.id.hiddenTextView);
        hiddenTextView.addTextChangedListener(this);
        beaconHelper = new BeaconHelper(this, hiddenTextView, 1);
        startReadingBeaconsButton = (Button) findViewById(R.id.startReadingBeaconsButton);
        stopReadingBeaconsButton = (Button) findViewById(R.id.stopReadingBeaconsButton);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        buttons = new Button[4];
        buttons[0] = (Button) findViewById(R.id.b149);
        buttons[1] = (Button) findViewById(R.id.b649);
        buttons[2] = (Button) findViewById(R.id.b831);
        buttons[3] = (Button) findViewById(R.id.b947);
        startMqtt();

    }

    public void detectBeaconInMap(View v){
        beaconHelper.startDetectingBeacons();
        changeStartButtonColors(stopReadingBeaconsButton, v);
    }

    public void stopDetectBeaconInMap(View v){
        beaconHelper.stopDetectingBeacons();
        changeStartButtonColors(startReadingBeaconsButton, v);
    }

    private void changeStartButtonColors(View able, View disable){

        able.setEnabled(true);
        able.setAlpha(1);

        disable.setEnabled(false);
        disable.setAlpha(.5f);
    }

    public void changeColor(){
        CharSequence text = hiddenTextView.getText();

        text = text.subSequence(text.length()-3, text.length());
        String comparingText = text.toString();
        String buttonTextString ="";
        for (Button b : buttons){
            b.setBackgroundColor(Color.RED);
            buttonTextString = b.getText().toString();
            if (comparingText.equals(buttonTextString)){
                b.setBackgroundColor(Color.GREEN);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        changeColor();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        inviaMessaggio("L'utente CELL ORLANDO è passato dal beacon con id: "+ hiddenTextView.getText()+" al tempo "+ dateFormat.format(date) );
    }

    private void inviaMessaggio(String messaggio){
        MqttMessage message = new MqttMessage(messaggio.getBytes());
        try {
            mqttHelper.publica(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

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

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
