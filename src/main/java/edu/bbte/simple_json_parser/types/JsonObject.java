package edu.bbte.simple_json_parser.types;

import java.util.ArrayList;

// Composite
public class JsonObject implements JsonNode {
    private final ArrayList<JsonNode> children;

    private final String key;

    public JsonObject(Builder builder) {
        this.children = builder.children;
        this.key = builder.key;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private ArrayList<JsonNode> children;

        private String key = null;

        private Builder newChild;

        private JsonArray.Builder newArrayChild;

        public Builder() {
        }

        public JsonObject build() {
            return new JsonObject(this);
        }

        public Builder startObject() {
            children = new ArrayList<>();
            return this;
        }

        public Builder startObject(String key) {
            if (newArrayChild != null) {
                newChild = JsonObject.newBuilder().startObject();
                newChild.setKey(null);
            } else {
                newChild = JsonObject.newBuilder().startObject();
                newChild.setKey(key);
            }
            return this;
        }

        public Builder endObject() {
            if (children != null && newChild != null) {
                if (newArrayChild != null) {
                    newArrayChild.addObject(newChild.build());
                    newChild = null;
                } else {
                    children.add(newChild.build());
                    newChild = null;
                }
                return this;
            }

            if (children == null) {
                throw new IllegalStateException("No children to end");
            }

            return this;
        }

        public Builder startArray(String key) {
            newArrayChild = JsonArray.newBuilder().setKey(key);
            return this;
        }

        public Builder endArray() {
            if (children != null && newArrayChild != null) {
                children.add(newArrayChild.build());
                newArrayChild = null;
            }
            return this;
        }

        public Builder addArray(JsonArray arr) {
            if (newChild != null) {
                newChild.addArray(arr);
            } else {
                children.add(arr);
            }
            return this;
        }

        public JsonArray.Builder getArrayBuilder() {
            return newArrayChild;
        }

        public Builder addProperty(String key, String value) {
            if (newChild != null) {
                newChild.addProperty(key, value);
            } else {
                children.add(new JsonString(key, value));
            }

            return this;
        }

        public Builder addProperty(String key, double value) {
            if (newChild != null) {
                newChild.addProperty(key, value);
            } else {
                children.add(new JsonNumber(key, value));
            }

            return this;
        }

        public Builder addProperty(String key, boolean value) {
            if (newChild != null) {
                newChild.addProperty(key, value);
            } else {
                children.add(new JsonBoolean(key, value));
            }

            return this;
        }

        public Builder addNullProperty(String key) {
            if (newChild != null) {
                newChild.addNullProperty(key);
            } else {
                children.add(new JsonNull());
            }

            return this;
        }

        public Builder addError(String error) {
            if (newChild != null) {
                newChild.addError(error);
            } else {
                children.add(new ErrorNode(error));
            }
            return this;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public String getKey() {
        return key;
    }
}
