package edu.bbte.simple_json_parser.types;

public class ErrorNode implements JsonNode{
    String error;

    public ErrorNode(String error){
        this.error = error;
    }
}
