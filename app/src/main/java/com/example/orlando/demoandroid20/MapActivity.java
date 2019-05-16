package com.example.orlando.demoandroid20;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.RangeNotifier;

public class MapActivity extends AppCompatActivity implements TextWatcher {

    private BeaconHelper beaconHelper;
    private Button startReadingBeaconsButton;
    private Button stopReadingBeaconsButton;
    private RangeNotifier rangeNotifier;
    private TextView hiddenTextView;
    private Button[] buttons;


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


    }

    public void detectGio(View v){
        beaconHelper.startDetectingBeacons();
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
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
