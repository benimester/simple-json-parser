package edu.bbte.simple_json_parser.types;

import edu.bbte.simple_json_parser.visitor.JsonVisitor;

/**
 * Component interface for the Composite pattern.
 * Defines the accept method for the Visitor pattern.
 */
public interface JsonNode {
    void accept(JsonVisitor visitor);
}
