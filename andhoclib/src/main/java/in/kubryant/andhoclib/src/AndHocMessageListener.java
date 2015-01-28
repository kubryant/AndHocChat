package in.kubryant.andhoclib.src;

import in.kubryant.andhoclib.interfaces.AndHocMessageListenerInterface;

public class AndHocMessageListener implements AndHocMessageListenerInterface {
    @Override
    public void onNewMessage(AndHocMessage message) {}

    @Override
    public void onNewService(AndHocMessage message) {}
}
