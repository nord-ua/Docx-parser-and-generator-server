package utils.parts;

import org.json.JSONObject;

public class ParsedPart {

    protected String key;
    protected String type;
    protected JSONObject data = new JSONObject();

    ParsedPart(String key, String type) {
        this.key = key;
        this.type = type;
    }

    ParsedPart(String key, String type, JSONObject data) {
        this(key, type);
        this.data = data;
    }

    public JSONObject getJson() {
        JSONObject result = new JSONObject();
        result.put("key", key);
        result.put("type", type);
        result.put("data", data);

        return result;
    }

    @Override
    public String toString() {
        return "ParsedPart{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
