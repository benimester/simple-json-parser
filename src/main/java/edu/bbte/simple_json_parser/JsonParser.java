package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JsonParser {
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

            String value = scanner.findWithinHorizon(
                            "\\{|\\[}|0|-?[1-9][0-9]*|-?[0-9]+\\.[0-9]+|true|false|null|\"" + "([^\"])*\"", 0)
                    .strip(); // value pattern

            char c = value.charAt(0);

            switch (value.charAt(0)) {
                case '{':
                    builder.startObject(key);
                    parse(builder);
                    break;
                case '[':
                    System.out.println("Arrays are not supported yet");
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
}
