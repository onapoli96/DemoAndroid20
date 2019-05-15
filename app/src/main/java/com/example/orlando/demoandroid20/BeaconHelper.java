package com.example.orlando.demoandroid20;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconHelper implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private Region mRegion;
    private Context context;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final long DEFAULT_SCAN_PERIOD_MS = 6000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";

    public BeaconHelper(Context context) {

        this.context = context;

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

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() == 0) {
            showToastMessage("Nessun beacon trovato");
        }

        for (Beacon beacon : beacons) {
            Log.d("bla", "ciao");
            showToastMessage("Ho trovato il beacon con id %1$s " + beacon.getId1());
            showToastMessage("Si trova a questa distanza: " + beacon.getDistance());
            //segnaleReceived.setText(beacon.getId1().toString());
        }
    }
}
