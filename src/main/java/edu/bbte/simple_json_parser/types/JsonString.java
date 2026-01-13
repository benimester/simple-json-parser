package edu.bbte.simple_json_parser.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonString implements JsonNode {
    private String key;
    private String value;

    public JsonString(String key, String value){
        this.key = key;
        this.value = value;
    }
}
