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

        @Override
        public String toString() {
            return "Item{" +
                    "key='" + key + '\'' +
                    ", type=" + type +
                    ", content='" + content + '\'' +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    public Map<String, Item> data;
    public Item item(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "data=" + data +
                '}';
    }
}
