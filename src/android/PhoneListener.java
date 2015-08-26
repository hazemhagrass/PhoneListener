package com.badrit.PhoneListener;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhoneListener extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray data,
                           CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();

        if (action.equals("monitor_signal")) {
            monitorSignal();

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
        }

        return false;
    }

    private void monitorSignal(){
        TelephonyManager phonyManager  = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener mListener = new PhoneStateListener(){
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {

                int strengthAmplitude = signalStrength.getCdmaDbm();
                String cdmaStrength = String.valueOf(strengthAmplitude);

                super.onSignalStrengthsChanged(signalStrength);

                try {
                    JSONObject data = new JSONObject();
                    data.put("cdma_strength", cdmaStrength);
                    data.put("cdma_dbm", signalStrength.getCdmaDbm());
                    data.put("cdma_ecio", signalStrength.getCdmaEcio());
                    data.put("evo_dbn", signalStrength.getEvdoDbm());
                    data.put("evo_ecio", signalStrength.getEvdoEcio());
                    data.put("evo_snr", signalStrength.getEvdoSnr());
                    data.put("gsm_bit_error_rate", signalStrength.getGsmBitErrorRate());
                    data.put("gsm_signal_strength", signalStrength.getGsmSignalStrength());

                    Log.d("PhoneStateListener", data.toString());
                    callbackContext.success(data);
                } catch (Exception e) {
                    Log.v("ContactPicker", "Parsing contact failed: " + e.getMessage());
                    callbackContext.error("Parsing contact failed: " + e.getMessage());
                }

            }
        };
        phonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Toast.makeText(context, "Monitoring Start", Toast.LENGTH_SHORT).show();
    }

    public void getNetworkSignalStrength()
    {

    }
}
