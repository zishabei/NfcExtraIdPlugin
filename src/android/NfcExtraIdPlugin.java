package nfcExtraIdPlugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;

/**
 * Created by wangqiang on 2017/4/20.
 */

public class NfcExtraIdPlugin extends CordovaPlugin {
    private String mStr;
    public NfcAdapter mNfcAdapter;
    public static IntentFilter[] mIntentFilter = null;
    public static PendingIntent mPendingIntent = null;
    private CallbackContext callbackContext;
	private boolean startOrNot;
    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
        NfcInit(cordova.getActivity());
        mNfcAdapter.enableForegroundDispatch(this.cordova.getActivity(), mPendingIntent, mIntentFilter, null);
        Log.i(TAG, "extra_id: " + mStr);
        if (action.equals("extra_id")) {
            if (mNfcAdapter == null) {
                callbackContext.error("NFC function is not supported");
                Log.i(TAG, "NFC function is not supported");
            } else {
                Log.i(TAG, "support nfc function");
                if (mNfcAdapter.isEnabled()) {
                    if ("extra_id".equals(action)) {
                        callbackContext.success("Start of read task");
                        startOrNot = true;
                        return true;
                    }
                } else {
                    callbackContext.error("Please open nfc");
                }
            }
        } else {
        	startOrNot = false;
            callbackContext.success("Stop of read task");
            return true;
        }
        return super.execute(action, rawArgs, callbackContext);
    }

    private static final String TAG = "GetNfcExtraId";

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        mNfcAdapter.enableForegroundDispatch(this.cordova.getActivity(), mPendingIntent, mIntentFilter, null);
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        mNfcAdapter.disableForegroundDispatch(this.cordova.getActivity());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            mStr = readIdFromTag(intent);
            Log.i(TAG, "onNewIntent: " + readIdFromTag(intent));
            if(startOrNot){
            	showDialog(mStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String mStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
        builder.setTitle("Alert");
        builder.setMessage("EXTRA_ID:"+mStr);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                callbackContext.error("");
//            }
//        });
        if (mStr != null && !"".equals(mStr)) {
            builder.show();
        }
    }

    /**
     * check NFC is open or not.
     */
    public static NfcAdapter NfcCheck(Activity activity) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            return null;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                activity.startActivity(setNfc);
            }
        }
        return mNfcAdapter;
    }

    /**
     * init nfc
     */
    public void NfcInit(Activity activity) {
        mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter filter1 = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mIntentFilter = new IntentFilter[]{filter, filter1, filter2};
    }

    /**
     * read nfc data
     */
    public String readFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                return readResult;
            }
        }
        return "";
    }

    /**
     * read nfc EXTRA_ID
     */
    public String readIdFromTag(Intent intent) throws UnsupportedEncodingException {
        byte[] arr = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String id = ByteArrayToHexString(arr);
        return id;
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        if (inarray != null) {
            for (j = 0; j < inarray.length; ++j) {
                in = (int) inarray[j] & 0xff;
                i = (in >> 4) & 0x0f;
                out += hex[i];
                i = in & 0x0f;
                out += hex[i];
            }
        }
        return out;
    }
}
