package in.kubryant.andhoclib.interfaces;

import in.kubryant.andhoclib.src.AndHocMessage;
import in.kubryant.andhoclib.src.AndHocMessageListener;

public interface AndHocServiceInterface {
    public void addListener(AndHocMessageListener listener);
    public void removeListener();
    public void listen();
    public void listenTimer(int seconds);
    public void stopListen();
    public void broadcast(AndHocMessage record);
    public void stopBroadcast();
}
