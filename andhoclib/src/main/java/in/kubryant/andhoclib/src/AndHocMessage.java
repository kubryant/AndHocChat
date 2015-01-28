package in.kubryant.andhoclib.src;

import java.util.HashMap;

import in.kubryant.andhoclib.interfaces.AndHocMessageInterface;

public class AndHocMessage implements AndHocMessageInterface {
    HashMap<String, String> record = new HashMap();

    public AndHocMessage() {}

    public AndHocMessage(HashMap<String, String> rec) {
        record = rec;
    }

    @Override
    public void setMessage(HashMap<String, String> rec) {
        record = rec;
    }

    @Override
    public HashMap<String, String> getMessage() {
        return record;
    }

    @Override
    public void add(String key, String value) {
        record.put(key, value);
    }
}
