package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonArray;
import edu.bbte.simple_json_parser.types.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JsonParser {
    private static final String VALUE_PATTERN =
            "\\{|\\[|-?[0-9]+\\.[0-9]+|-?[1-9][0-9]*|0|true|false|null|\"([^\"])*\"";
    private static final String ARRAY_VALUE_PATTERN =
            "\\{|\\[|\\]|-?[0-9]+\\.[0-9]+|-?[1-9][0-9]*|0|true|false|null|\"([^\"])*\"";

    private final File file;
    private Scanner scanner;

    public JsonParser(File file) {
        this.file = file;
    }

    public JsonObject startReading() {
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }

        JsonObject.Builder builder = JsonObject.newBuilder();

        parse(builder);

        return builder.build();
    }

    public void parse(JsonObject.Builder builder) {

        while (scanner.hasNext()) {
            if (scanner.hasNext("\\s*\\{")) {
                scanner.skip("\\s*\\{");
                builder.startObject();
            }

            String key = scanner.findWithinHorizon("\"[^\"]*\"", 0);
            key = key.substring(1, key.length() - 1);

            scanner.skip("\\s*:");

            String value = scanner.findWithinHorizon(VALUE_PATTERN, 0).strip();

            switch (value.charAt(0)) {
                case '{':
                    builder.startObject(key);
                    parse(builder);
                    break;
                case '[':
                    builder.startArray(key);
                    parseArray(builder.getArrayBuilder());
                    builder.endArray();
                    break;
                case '"':
                    builder.addProperty(key, value.substring(1, value.length() - 1));
                    break;
                case 't', 'f':
                    builder.addProperty(key, Boolean.parseBoolean(value));
                    break;
                case 'n':
                    builder.addNullProperty(key);
                    break;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-':
                    builder.addProperty(key, Double.parseDouble(value));
                    break;
                default:
                    builder.addError("Invalid value: '" + value + "'");
            }

            String nextToken = scanner.findWithinHorizon("\\s*[,}]", 0);
            if (nextToken != null) {
                nextToken = nextToken.strip();
                if (nextToken.equals("}")) {
                    builder.endObject();
                    return;
                }
            }

        }
    }

    public void parseArray(JsonArray.Builder arrayBuilder) {
        while (scanner.hasNext()) {
            // Check for empty array or end of array
            if (scanner.hasNext("\\s*\\]")) {
                scanner.skip("\\s*\\]");
                return;
            }

            String value = scanner.findWithinHorizon(ARRAY_VALUE_PATTERN, 0).strip();

            if (value.equals("]")) {
                return;
            }

            switch (value.charAt(0)) {
                case '{':
                    // Nested object inside array
                    JsonObject.Builder nestedObjectBuilder = JsonObject.newBuilder().startObject();
                    parseNestedObject(nestedObjectBuilder);
                    arrayBuilder.addObject(nestedObjectBuilder.build());
                    break;
                case '[':
                    // Nested array inside array
                    JsonArray.Builder nestedArrayBuilder = JsonArray.newBuilder();
                    parseArray(nestedArrayBuilder);
                    arrayBuilder.addArray(nestedArrayBuilder.build());
                    break;
                case '"':
                    arrayBuilder.addElement(value.substring(1, value.length() - 1));
                    break;
                case 't', 'f':
                    arrayBuilder.addElement(Boolean.parseBoolean(value));
                    break;
                case 'n':
                    arrayBuilder.addNull();
                    break;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-':
                    arrayBuilder.addElement(Double.parseDouble(value));
                    break;
                default:
                    arrayBuilder.addError("Invalid value: '" + value + "'");
            }

            // Check for comma or end of array
            String nextToken = scanner.findWithinHorizon("\\s*[,\\]]", 0);
            if (nextToken != null) {
                nextToken = nextToken.strip();
                if (nextToken.equals("]")) {
                    return;
                }
            }
        }
    }

    private void parseNestedObject(JsonObject.Builder builder) {
        while (scanner.hasNext()) {
            String key = scanner.findWithinHorizon("\"[^\"]*\"", 0);
            key = key.substring(1, key.length() - 1);

            scanner.skip("\\s*:");

            String value = scanner.findWithinHorizon(VALUE_PATTERN, 0).strip();

            switch (value.charAt(0)) {
                case '{':
                    builder.startObject(key);
                    parseNestedObject(builder);
                    builder.endObject();
                    break;
                case '[':
                    JsonArray.Builder nestedArrayBuilder = JsonArray.newBuilder().setKey(key);
                    parseArray(nestedArrayBuilder);
                    builder.addArray(nestedArrayBuilder.build());
                    break;
                case '"':
                    builder.addProperty(key, value.substring(1, value.length() - 1));
                    break;
                case 't', 'f':
                    builder.addProperty(key, Boolean.parseBoolean(value));
                    break;
                case 'n':
                    builder.addNullProperty(key);
                    break;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-':
                    builder.addProperty(key, Double.parseDouble(value));
                    break;
                default:
                    builder.addError("Invalid value: '" + value + "'");
            }

            String nextToken = scanner.findWithinHorizon("\\s*[,}]", 0);
            if (nextToken != null) {
                nextToken = nextToken.strip();
                if (nextToken.equals("}")) {
                    return;
                }
            }
        }
    }
}
