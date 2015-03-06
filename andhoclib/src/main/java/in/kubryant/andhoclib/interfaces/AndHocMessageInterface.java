package in.kubryant.andhoclib.interfaces;

import java.util.Map;

public interface AndHocMessageInterface {
    public String get(String name);
    public void setRecord(Map<String, String> rec);
    public Map<String, String> getRecord();
    public void add(String key, String value);
}
