package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

// Special Case pattern - represents parsing errors without throwing exceptions
public class ErrorNode implements JsonNode {
    private final String error;

    public ErrorNode(String error) {
        this.error = error;
    }

    @Override
    public void accept(JsonVisitor visitor) {
        visitor.visit(this);
    }

    public String getError() {
        return error;
    }
}
