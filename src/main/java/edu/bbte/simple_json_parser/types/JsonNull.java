package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JsonNull implements JsonNode {
    private final String key;

    public JsonNull() {
        this.key = null;
    }

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }
}
