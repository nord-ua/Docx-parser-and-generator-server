package utils.parts;

public class TextPart extends ParsedPart {

    public TextPart(String key, String text) {
        super(key, "text");
        data.put("text", text == null ? "" : text);
    }
}
