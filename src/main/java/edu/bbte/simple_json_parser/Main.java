package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonObject;
import edu.bbte.simple_json_parser.visitor.JsonFormatterVisitor;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Example: Parse from file
        String resourcePath = Main.class.getClassLoader().getResource("test.json").getPath();
        JsonParser jsonParser = new JsonParser(new File(resourcePath));
        JsonObject finalObject = jsonParser.startReading();

        // Use the Visitor pattern to format the output
        JsonFormatterVisitor visitor = new JsonFormatterVisitor();
        finalObject.accept(visitor);
        System.out.println(visitor.getResult());
    }
}
