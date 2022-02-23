package com.example.demolocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
    private String senderTel;
    private LocationManager manager;
    private LocationListener listener;

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(senderTel, null,
                        "http://maps.google.com/maps?q=" +
                                location.getLatitude() + "," +
                                location.getLongitude(), null, null);
                manager.removeUpdates(listener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            senderTel = "";
            Object[] pdus = ((Object[]) bundle.get("pdus"));
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (i == 0) {

                    senderTel = msgs[i].getOriginatingAddress();
                }
                str += msgs[i].getMessageBody().toString();
            }
            if (str.startsWith("Where are you?")) {
                manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                listener = new MyLocationListener();

                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1000, listener);
                this.abortBroadcast();
            }
        }
    }
}