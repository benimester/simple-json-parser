package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

// Leaf node in the Composite pattern (also represents null value - Special Case pattern)
public class JsonNull implements JsonNode {
    private final String key;

    public JsonNull() {
        this.key = null;
    }

    public JsonNull(String key) {
        this.key = key;
    }

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }

    public String getKey() {
        return key;
    }
}
