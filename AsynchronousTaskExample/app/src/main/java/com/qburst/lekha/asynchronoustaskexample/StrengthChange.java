package com.qburst.lekha.asynchronoustaskexample;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by user on 20/10/16.
 */

public class StrengthChange extends PhoneStateListener {
    Context mContext;
    public String networkType;
    public TelephonyManager tManager;
    public StrengthChange(Context context) {
        mContext = context;
    }
    @Override
    public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {

        // get the signal strength (a value between 0 and 31)
        int strengthAmplitude = signalStrength.getGsmSignalStrength();
        tManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        int nt = tManager.getNetworkType();
        switch (nt) {
            case 1: networkType = "GPRS ~ 100 kbps";
                break;
            case 2: networkType =  "EDGE ~ 50-100 kbps";
                break;
            case 3: networkType =  "UMTS ~ 400-7000 kbps";
                break;
            case 8: networkType =  "HSDPA ~ 2-14 Mbps";
                break;
            case 9: networkType =  "HSUPA ~ 1-23 Mbps";
                break;
            case 10:networkType =  "HSPA ~ 700-1700 kbps";
                break;
            case 11: networkType = "IDEN ~25 kbps";
                break;
            case 13: networkType = "LTE ~ 10+ Mbps";
                break;
            case 15:networkType = "HSPA+ ~ 10-20 Mbps";
                break;
            default:networkType =  "UNKNOWN";
                break;
        }
        Toast.makeText(mContext, networkType+"Amp: "+String.valueOf(strengthAmplitude), Toast.LENGTH_LONG).show();
        super.onSignalStrengthsChanged(signalStrength);
    }

}
