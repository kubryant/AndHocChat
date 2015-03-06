package in.kubryant.andhoclib.src;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import in.kubryant.andhoclib.interfaces.AndHocMessengerInterface;

public class AndHocMessenger implements AndHocMessengerInterface{
    private final String TAG = AndHocMessenger.class.getSimpleName();

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pServiceInfo mServiceInfo = null;

    public AndHocMessenger(final Context context) {
        Log.d(TAG, "AndHocMessenger Constructor");
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, Looper.getMainLooper(), null);
    }

    @Override
    public void broadcast(final Context context, AndHocMessage record) {
        mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance("_msg", "_presence._tcp", record.getRecord());
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
