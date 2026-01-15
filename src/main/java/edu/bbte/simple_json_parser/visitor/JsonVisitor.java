package edu.bbte.simple_json_parser.visitor;

import edu.bbte.simple_json_parser.types.*;

/**
 * Visitor pattern - defines visit methods for each JsonNode type.
 * Allows adding new operations to the JSON tree without modifying node classes.
 */
public interface JsonVisitor {
    void visit(JsonObject jsonObject);
    void visit(JsonArray jsonArray);
    void visit(JsonString jsonString);
    void visit(JsonNumber jsonNumber);
    void visit(JsonBoolean jsonBoolean);
    void visit(JsonNull jsonNull);
    void visit(ErrorNode errorNode);
}

