package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorNode implements JsonNode {
    private final String error;

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }
}
