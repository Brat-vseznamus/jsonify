package jsonify.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonClassDescription implements JsonDescription {
    private final Map<String, JsonDescription> properties;

    public JsonClassDescription() {
        this.properties = new HashMap<>();
    }

    public void addProperty(String name, JsonDescription description) {
        properties.put(name, description);
    }

    // not very effective
    @Override
    public String toJson() {
        return "{" +
                properties.entrySet().stream()
                    .map(entry -> String.format(
                            "\"%s\": %s",
                            entry.getKey(),
                            entry.getValue().toJson())
                    )
                    .collect(Collectors.joining(", ")) +
        "}";
    }

    @Override
    public String toJson(int indent) {
        return toJson(indent, 0);
    }

    @Override
    public String toJson(int indent, int h) {
        if (properties.isEmpty()) {
            return "{}";
        }
        return "{\n" +
                properties.entrySet().stream()
                        .map(entry -> String.format(
                                " ".repeat(indent).repeat(h + 1) + "\"%s\": %s",
                                entry.getKey(),
                                entry.getValue().toJson(indent, h + 1))
                        )
                        .collect(Collectors.joining(",\n")) +
                "\n" +
                " ".repeat(indent).repeat(h) + "}";
    }
}
