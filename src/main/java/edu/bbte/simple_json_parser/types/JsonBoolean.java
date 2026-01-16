package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JsonBoolean implements JsonNode {
    private final String key;
    private final boolean value;

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }
}
