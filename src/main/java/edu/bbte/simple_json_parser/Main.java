package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonObject;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        JsonParser jsonParser = new JsonParser(new File("/Users/benimester/Documents/University/Master/I/Mintak/Projekt/simple-json-parser/src/main/resources/test.json"));
        JsonObject finalObject = jsonParser.startReading();
        System.out.println("JSON parsed successfully.");
    }
}
