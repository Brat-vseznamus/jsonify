package jsonify.elements;

public class JsonPrimitiveDescription implements JsonDescription {
    private final String name;

    public JsonPrimitiveDescription(String name) {
        this.name = name;
    }

    @Override
    public String toJson() {
        return "\"" + name + "\"";
    }

    @Override
    public String toJson(int indent) {
        return toJson();
    }

    @Override
    public String toJson(int indent, int h) {
        return toJson();
    }
}
