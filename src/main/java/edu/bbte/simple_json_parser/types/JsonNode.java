package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

public interface JsonNode {
    void accept(JsonVisitor visitor);
}
