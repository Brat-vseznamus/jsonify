package jsonify.elements;

public class JsonMapDescription implements JsonDescription {
    private final JsonDescription keyType;
    private final JsonDescription valueType;

    public JsonMapDescription(JsonDescription keyType, JsonDescription valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public String toJson() {
        return "[{\n" + "\"key\": " + keyType.toJson() + ", \"value\": " + valueType.toJson() + "}]";
    }

    @Override
    public String toJson(int indent) {
        return toJson(indent, 0);
    }

    @Override
    public String toJson(int indent, int h) {
        return "[{\n" +
                " ".repeat(indent).repeat(h + 1) +
                "\"key\": " + keyType.toJson(indent, h + 1) + ",\n" +
                " ".repeat(indent).repeat(h + 1) +
                "\"value\": " + valueType.toJson(indent, h + 1) + "\n" +
                " ".repeat(indent).repeat(h) + "}]";
    }
}
