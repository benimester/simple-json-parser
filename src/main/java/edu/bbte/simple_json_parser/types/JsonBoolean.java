package edu.bbte.simple_json_parser.types;


public class JsonBoolean implements JsonNode {
    private String key;
    private boolean value;

    public JsonBoolean(String key, boolean value){
        this.key = key;
        this.value = value;
    }
}
