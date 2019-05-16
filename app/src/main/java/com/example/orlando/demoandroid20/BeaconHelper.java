package com.example.orlando.demoandroid20;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BeaconHelper implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private Region mRegion;
    private Context context;
    private TextView segnaleReceived;
    private static final long DEFAULT_SCAN_PERIOD_MS = 6000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";

    private int numActivity;

    public BeaconHelper(Context context,TextView segnaleReceived, int numActivity) {

        this.numActivity = numActivity;

        this.context = context;
        this.segnaleReceived = segnaleReceived;

        mBeaconManager = BeaconManager.getInstanceForApplication(context);

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        ArrayList<Identifier> identifiers = new ArrayList<>();

        mRegion = new Region(ALL_BEACONS_REGION, identifiers);
    }

    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            showToastMessage("Cercando beacon..");

        } catch (RemoteException e) {
            Log.d("TAG", "Exception" + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);

    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent,  serviceConnection, i);

    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        String tutti = " ";
        int i = 0;
        if (beacons.size() == 0) {
            showToastMessage(context.getString(R.string.no_beacons_detected));
        }
        if(numActivity == 0) {
            for (Beacon beacon : beacons) {
                Log.d("bla", "ciao");
                i++;
                int distanza = (int) (beacon.getDistance() * 100);
                tutti = tutti + "ID: " + beacon.getId2().toString() + " - distanza: " + distanza + "\n";

                segnaleReceived.setText(tutti);
            }
        }
        else if (numActivity == 1){
            segnaleReceived.setText(""+getMinimumDistance(beacons));
        }
    }

    private String getMinimumDistance(Collection <Beacon> beacons){
        int min = 0;
        String minBeacon = "";
        boolean flag = true;
        for(Beacon beacon: beacons){
            if(flag){
                min = (int)(beacon.getDistance() * 100);
                minBeacon = beacon.getId2().toString();
                flag = false;
            }
            if (((int)(beacon.getDistance() * 100)) < min){
                min = (int)(beacon.getDistance() * 100);
                minBeacon = beacon.getId2().toString();
            }
        }
        return minBeacon;

    }

    public void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            showToastMessage(context.getString(R.string.stop_looking_for_beacons));
        } catch (RemoteException e) {
            Log.d("Errore", "Errore: " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }

    public void startDetectingBeacons() {
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);
        mBeaconManager.bind(this);
    }


    public void clear(){
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }
}
