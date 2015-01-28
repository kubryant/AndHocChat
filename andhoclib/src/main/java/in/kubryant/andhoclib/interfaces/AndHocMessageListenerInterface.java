package in.kubryant.andhoclib.interfaces;

import in.kubryant.andhoclib.src.AndHocMessage;

public interface AndHocMessageListenerInterface {
    public void onNewMessage(AndHocMessage message);
    public void onNewService(AndHocMessage message);
}
