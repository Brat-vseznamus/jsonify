package jsonify.elements;

public interface JsonDescription {
    String toJson();
    String toJson(int indent);
    String toJson(int indent, int h);
}
