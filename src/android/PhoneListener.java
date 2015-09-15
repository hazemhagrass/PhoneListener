package com.badrit.PhoneListener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhoneListener extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;
    private JSONObject data;
    private String carrierName;

    @Override
    public boolean execute(String action, JSONArray data,
                           CallbackContext callbackContext) {
        if (action.equals("monitor_signal")) {
            this.callbackContext = callbackContext;
            this.context = cordova.getActivity().getApplicationContext();
            monitorSignal();

            PluginResult r = new PluginResult(PluginResult.Status.OK, data);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
        }

        if (action.equals("carrier_name")) {
            getCarrierName();
            callbackContext.success(carrierName);
        }

        return true;
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
                    data = new JSONObject();
                    data.put("is_gsm", signalStrength.isGsm());
                    data.put("cdma_strength", cdmaStrength);
                    data.put("cdma_dbm", signalStrength.getCdmaDbm());
                    data.put("cdma_ecio", signalStrength.getCdmaEcio());
                    data.put("evo_dbn", signalStrength.getEvdoDbm());
                    data.put("evo_ecio", signalStrength.getEvdoEcio());
                    data.put("evo_snr", signalStrength.getEvdoSnr());
                    data.put("gsm_bit_error_rate", signalStrength.getGsmBitErrorRate());
                    data.put("gsm_signal_strength", signalStrength.getGsmSignalStrength());

                    Log.d("PhoneStateListener", data.toString());
                    PluginResult r = new PluginResult(PluginResult.Status.OK, data);
                    r.setKeepCallback(true);
                    callbackContext.sendPluginResult(r);

                } catch (Exception e) {
                    Log.v("ContactPicker", "Parsing contact failed: " + e.getMessage());
                    callbackContext.error("Parsing contact failed: " + e.getMessage());
                }

            }
        };
        phonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Toast.makeText(context, "Monitoring Start", Toast.LENGTH_SHORT).show();
    }

    public void getCarrierName(){
        TelephonyManager phonyManager  = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = phonyManager.getNetworkOperatorName();

        Log.d("carrierName", carrierName);
    }
}