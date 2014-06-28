package utils.parts;

public class EditPart extends ParsedPart {

    public EditPart(String key, String hint) {
        super(key, "edit");
        data.put("hint", hint);
    }
}
