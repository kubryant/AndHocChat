package in.kubryant.andhoclib.interfaces;

import android.content.Context;

import in.kubryant.andhoclib.src.AndHocMessage;

public interface AndHocMessengerInterface {
    public void broadcast(final Context context, AndHocMessage record);
    public void stopBroadcast(final Context context);
}
