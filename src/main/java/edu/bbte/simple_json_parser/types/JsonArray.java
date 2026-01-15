package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite pattern - represents a JSON array that can contain child nodes.
 * Uses the Builder pattern for construction.
 */
public class JsonArray implements JsonNode {
    private final ArrayList<JsonNode> elements;
    private final String key;

    public JsonArray(Builder builder) {
        this.elements = builder.elements;
        this.key = builder.key;
    }

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }

    public String getKey() {
        return key;
    }

    public List<JsonNode> getElements() {
        return elements;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ArrayList<JsonNode> elements;
        private String key = null;

        public Builder() {
            elements = new ArrayList<>();
        }

        public JsonArray build() {
            return new JsonArray(this);
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder addElement(String value) {
            elements.add(new JsonString(null, value));
            return this;
        }

        public Builder addElement(double value) {
            elements.add(new JsonNumber(null, value));
            return this;
        }

        public Builder addElement(boolean value) {
            elements.add(new JsonBoolean(null, value));
            return this;
        }

        public Builder addNull() {
            elements.add(new JsonNull());
            return this;
        }

        public Builder addObject(JsonObject obj) {
            elements.add(obj);
            return this;
        }

        public Builder addArray(JsonArray arr) {
            elements.add(arr);
            return this;
        }

        public Builder addError(String error) {
            elements.add(new ErrorNode(error));
            return this;
        }
    }
}
