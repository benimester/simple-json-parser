package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonObject;
import edu.bbte.simple_json_parser.visitor.JsonFormatterVisitor;

public class TestParser {
    public static void main(String[] args) {
        System.out.println("=== Test 1: Missing closing curly bracket ===");
        String test1 = "{\"name\": \"John\", \"age\": 30";
        testParse(test1);

        System.out.println("\n=== Test 2: Missing string closing quote ===");
        String test2 = "{\"name\": \"John, \"age\": 30}";
        testParse(test2);

        System.out.println("\n=== Test 3: Missing array closing bracket ===");
        String test3 = "{\"items\": [1, 2, 3, \"other\": [4, 5]}";
        testParse(test3);

        System.out.println("\n=== Test 4: Valid JSON ===");
        String test4 = "{\"name\": \"John\", \"age\": 30, \"items\": [1, 2, 3]}";
        testParse(test4);

        System.out.println("\n=== Test 5: Missing key closing quote ===");
        String test5 = "{\"name: \"John\"}";
        testParse(test5);
    }

    private static void testParse(String json) {
        System.out.println("Input: " + json);
        try {
            JsonParser parser = new JsonParser(json);
            JsonObject result = parser.startReading();
            JsonFormatterVisitor visitor = new JsonFormatterVisitor();
            result.accept(visitor);
            String output = visitor.getResult();
            System.out.println("Output:\n" + output);
            if (output.contains("/* Error:")) {
                System.out.println("Status: PARSED WITH ERRORS");
            } else {
                System.out.println("Status: PARSED SUCCESSFULLY");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

