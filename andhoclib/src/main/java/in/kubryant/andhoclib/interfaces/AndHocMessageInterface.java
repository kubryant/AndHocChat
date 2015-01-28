package in.kubryant.andhoclib.interfaces;

import java.util.HashMap;

public interface AndHocMessageInterface {
    public void setMessage(HashMap<String, String> rec);
    public HashMap<String, String> getMessage();
    public void add(String key, String value);
}
