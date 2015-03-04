package in.kubryant.andhoclib.src;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.Looper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import in.kubryant.andhoclib.interfaces.AndHocServiceInterface;

// TO DO:
// ALERT IF WIFI OFF
// AUTO LISTEN WHEN PEERS CHANGE
// TURN INTO SERVICE
// TURN OFF BROADCAST AFTER
// TOASTS INTO LOGS

public class AndHocService implements AndHocServiceInterface {
    private AndHocMessageListener mListener = null;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pServiceInfo mServiceInfo = null;

    public AndHocService(Context context) {
        super();
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, Looper.getMainLooper(), null);
    }

    @Override
    public void addListener(AndHocMessageListener listener) {
        mListener = listener;
    }

    @Override
    public void removeListener() {
        mListener = null;
    }

    @Override
    public void listen(final Context context) {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice) {
                //Toast.makeText(context, "Record available: " + record.toString(), Toast.LENGTH_SHORT).show();
                AndHocMessage message = new AndHocMessage((HashMap<String, String>) record);
                mListener.onNewMessage(message);
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice resourceType) {
                Toast.makeText(context, "Service available: " + resourceType.deviceName, Toast.LENGTH_SHORT).show();
            }
        };
        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance("_msg", "_presence._tcp");
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(context, "Services discovered!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(context, "Service discovery failed (" + reason + ")", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(context, "Listening", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Service request failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void stopListen(final Context context) {
        mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Listening stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Listening stop failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void broadcast(final Context context, AndHocMessage record) {
        //if(mServiceInfo != null) {
        //    stopBroadcast(context);
        //}

        mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance("_msg", "_presence._tcp", record.getMessage());
        mManager.addLocalService(mChannel, mServiceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Message broadcasting", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Broadcasting failed (" + reason + ")", Toast.LENGTH_SHORT).show();
                mServiceInfo = null;
            }
        });
    }

    @Override
    public void stopBroadcast(final Context context) {
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Broadcast stopped", Toast.LENGTH_SHORT).show();
                mServiceInfo = null;
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Broadcast stop failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }
}