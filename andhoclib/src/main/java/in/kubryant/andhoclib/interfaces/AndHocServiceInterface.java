package in.kubryant.andhoclib.interfaces;

import android.content.Context;

import in.kubryant.andhoclib.src.AndHocMessage;
import in.kubryant.andhoclib.src.AndHocMessageListener;

public interface AndHocServiceInterface {
    public void addListener(AndHocMessageListener listener);
    public void removeListener();
    public void listen(final Context context);
    public void stopListen(final Context context);
    public void broadcast(final Context context, AndHocMessage record);
    public void stopBroadcast(final Context context);
}
