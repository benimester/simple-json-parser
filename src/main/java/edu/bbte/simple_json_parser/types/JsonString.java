package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Leaf node in the Composite pattern
@Getter
@AllArgsConstructor
public class JsonString implements JsonNode {
    private final String key;
    private final String value;

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }
}
