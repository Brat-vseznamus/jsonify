package jsonify.elements;

public class JsonArrayDescription implements JsonDescription {
    private final JsonDescription type;

    public JsonArrayDescription(JsonDescription type) {
        this.type = type;
    }

    @Override
    public String toJson() {
        return "[" + type.toJson() + "]";
    }

    @Override
    public String toJson(int indent) {
        return toJson(indent, 0);
    }

    @Override
    public String toJson(int indent, int h) {
        return "[\n" +
                " ".repeat(indent).repeat(h + 1) +
                type.toJson(indent, h + 1) +
                "\n" +
                " ".repeat(indent).repeat(h) +
                "]";
    }
}
