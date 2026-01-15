package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

// Leaf node in the Composite pattern
public class JsonBoolean implements JsonNode {
    private final String key;
    private final boolean value;

    public JsonBoolean(String key, boolean value) {
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

    public boolean getValue() {
        return value;
    }
}
