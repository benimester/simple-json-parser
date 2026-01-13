package edu.bbte.simple_json_parser.types;


public class JsonNumber implements JsonNode {
    private String key;
    private double value;

    public JsonNumber(String key, double value) {
        this.key = key;
        this.value = value;
    }
}
