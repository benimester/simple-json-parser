package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

// Leaf node in the Composite pattern
public class JsonString implements JsonNode {
    private final String key;
    private final String value;

    public JsonString(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
