package com.badrit.PhoneListener;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.NeighboringCellInfo;
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

import java.util.List;

public class PhoneListener extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;
    private JSONObject data;
    private JSONObject simInfo;

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

        if (action.equals("sim_info")) {
            getSimInfo();
            callbackContext.success(simInfo);
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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

    public void getSimInfo(){
        TelephonyManager phonyManager  = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            simInfo = new JSONObject();
            simInfo.put("carrier_name", phonyManager.getNetworkOperatorName());
            simInfo.put("network_operator", phonyManager.getNetworkOperator());
            String phoneType = "";
            switch(phonyManager.getPhoneType()) {
                case 0:
                    phoneType = "NONE";
                    break;
                case 1:
                    phoneType = "GSM";
                    break;
                case 2:
                    phoneType = "CDMA";
                    break;
                case 3:
                    phoneType = "SIP";
                    break;
            }
            simInfo.put("phone_type", phoneType);
            simInfo.put("simSerial_number", phonyManager.getSimSerialNumber());
            simInfo.put("subscriber_id", phonyManager.getSubscriberId());
            String simState = "";
            switch(phonyManager.getSimState()) {
            case 0:
                simState = "UNKNOWN";
                break;
            case 1:
                simState = "ABSENT";
                break;
            case 2:
                simState = "PIN REQUIRED";
                break;
            case 3:
                simState = "PUK REQUIRED";
                break;
            case 4:
                simState = "NETWORK LOCKED";
                break;
            case 5:
                simState = "READY";
                break;
            }
            simInfo.put("sim_state", simState);
            Log.d("simInfo", phonyManager.toString());
        } catch (Exception e) {
            Log.v("ContactPicker", "Parsing contact failed: " + e.getMessage());
            callbackContext.error("Parsing contact failed: " + e.getMessage());
        }
    }
}