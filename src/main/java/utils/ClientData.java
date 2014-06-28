package utils;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ClientData {
    public enum Type {
        @SerializedName("edit") EDIT,
        @SerializedName("date") DATE,
        @SerializedName("image") IMAGE
    }

    public class Item {
        public String key;
        public Type type;
        public String content;
        public int w, h;
    }

    public Map<String, Item> data;
    public Item item(String key) {
        return data.get(key);
    }

}
