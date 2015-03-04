package in.kubryant.andhoclib.src;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.kubryant.andhoclib.interfaces.AndHocServiceInterface;

// TODO:
// ALERT IF WIFI OFF
// AUTO LISTEN WHEN PEERS CHANGE
// TURN OFF BROADCAST AFTER
// TOASTS INTO LOGS

public class AndHocService extends Service implements AndHocServiceInterface {
    private AndHocMessageListener mListener = null;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pServiceInfo mServiceInfo = null;
    private final IBinder mBinder = new AndHocBinder();
    private Timer timer;
    private boolean listenTime = false;

    public class AndHocBinder extends Binder {
        public AndHocService getService() {
            return AndHocService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "AndHocService Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "AndHocService Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        return mBinder;
    }

    public AndHocService() {
        super();
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
    public void listen() {
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
                Toast.makeText(getApplicationContext(), "Service available: " + resourceType.deviceName, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Service discovery failed (" + reason + ")", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Listening", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Service request failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void listenTimer(int seconds) {
        if(listenTime) {
            timer.cancel();
            timer.purge();
            listenTime = false;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listen();
            }
        }, 0, seconds * 1000);
        listenTime = true;
    }

    @Override
    public void stopListen() {
        if(listenTime) {
            timer.cancel();
            timer.purge();
            listenTime = false;
        }

        mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Listening stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Listening stop failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void broadcast(AndHocMessage record) {
        mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance("_msg", "_presence._tcp", record.getMessage());
        mManager.addLocalService(mChannel, mServiceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Message broadcasting", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Broadcasting failed (" + reason + ")", Toast.LENGTH_SHORT).show();
                mServiceInfo = null;
            }
        });
    }

    @Override
    public void stopBroadcast() {
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Broadcast stopped", Toast.LENGTH_SHORT).show();
                mServiceInfo = null;
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Broadcast stop failed (" + reason + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }
}