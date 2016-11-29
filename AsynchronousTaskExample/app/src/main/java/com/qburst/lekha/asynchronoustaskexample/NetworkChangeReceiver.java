package com.qburst.lekha.asynchronoustaskexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by user on 20/10/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = MainActivity.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

    }
}
