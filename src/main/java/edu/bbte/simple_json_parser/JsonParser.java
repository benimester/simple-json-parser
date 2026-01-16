package edu.bbte.simple_json_parser;

import edu.bbte.simple_json_parser.types.JsonArray;
import edu.bbte.simple_json_parser.types.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JsonParser {
    private final File file;
    private final String jsonString;
    private Scanner scanner;

    public JsonParser(File file) {
        this.file = file;
        this.jsonString = null;
    }

    public JsonParser(String jsonString) {
        this.jsonString = jsonString;
        this.file = null;
    }

    public JsonObject startReading() {
        if (jsonString != null) {
            scanner = new Scanner(jsonString);
            scanner.useDelimiter("");
        } else if (file != null) {
            try {
                scanner = new Scanner(file);
                scanner.useDelimiter("");
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                e.printStackTrace();
            }
        }

        JsonObject.Builder builder = JsonObject.newBuilder();

        parse(builder);

        return builder.build();
    }

    private void skipWhitespace() {
        while (scanner.hasNext("\\s")) {
            scanner.next();
        }
    }

    private String readStringValue() {
        if (!scanner.hasNext("\"")) {
            return null;
        }
        scanner.next();
        StringBuilder sb = new StringBuilder("\"");

        while (scanner.hasNext()) {
            String ch = scanner.next();
            sb.append(ch);

            if (ch.equals("\"")) {
                return sb.toString();
            }
            if (ch.equals("\\") && scanner.hasNext()) {
                sb.append(scanner.next());
            }
            if (ch.equals("\n") || ch.equals("\r")) {
                return null;
            }
        }

        return null;
    }

    public void parse(JsonObject.Builder builder) {
        boolean foundClosingBrace = false;

        while (scanner.hasNext()) {
            skipWhitespace();

            if (scanner.hasNext("\\{")) {
                scanner.next();
                builder.startObject();
            }

            skipWhitespace();

            if (scanner.hasNext("\"")) {
                String key = readStringValue();
                if (key == null) {
                    builder.addError("Unterminated string in key");
                    return;
                }
                key = key.substring(1, key.length() - 1);

                skipWhitespace();

                if (!scanner.hasNext(":")) {
                    builder.addError("Missing colon after key: " + key);
                    return;
                }
                scanner.next();
                skipWhitespace();

                if (scanner.hasNext("[,}]")) {
                    builder.addError("Missing value for key: " + key);
                    return;
                }
                if (scanner.hasNext("\"")) {
                    String strValue = readStringValue();
                    if (strValue == null) {
                        builder.addError("Unterminated string for key: " + key);
                        return;
                    }
                    builder.addProperty(key, strValue.substring(1, strValue.length() - 1));
                } else if (scanner.hasNext("\\{")) {
                    scanner.next();
                    builder.startObject(key);
                    parse(builder);
                } else if (scanner.hasNext("\\[")) {
                    scanner.next();
                    builder.startArray(key);
                    parseArray(builder.getArrayBuilder());
                    builder.endArray();
                } else if (scanner.hasNext("t")) {
                    String value = scanner.findWithinHorizon("true", 4);
                    if (value != null && value.equals("true")) {
                        builder.addProperty(key, true);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("f")) {
                    String value = scanner.findWithinHorizon("false", 5);
                    if (value != null && value.equals("false")) {
                        builder.addProperty(key, false);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("n")) {
                    String value = scanner.findWithinHorizon("null", 4);
                    if (value != null && value.equals("null")) {
                        builder.addNullProperty(key);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("-") || scanner.hasNext("[0-9]")) {
                    String numValue = scanner.findWithinHorizon("-?[0-9]+(\\.[0-9]+)?", 50);
                    if (numValue != null) {
                        builder.addProperty(key, Double.parseDouble(numValue));
                    } else {
                        builder.addError("Invalid number for key: " + key);
                        return;
                    }
                } else {
                    builder.addError("Expected value after key: " + key);
                    return;
                }

                skipWhitespace();
                if (scanner.hasNext(",")) {
                    scanner.next();
                } else if (scanner.hasNext("}")) {
                    scanner.next();
                    builder.endObject();
                    foundClosingBrace = true;
                    return;
                } else if (scanner.hasNext()) {
                    builder.addError("Expected ',' or '}' in object");
                    return;
                } else {
                    builder.addError("Unclosed object: missing '}'");
                    return;
                }
            } else if (scanner.hasNext("}")) {
                scanner.next();
                builder.endObject();
                foundClosingBrace = true;
                return;
            } else {
                builder.addError("Expected key or closing '}'");
                return;
            }
        }

        if (!foundClosingBrace) {
            builder.addError("Unclosed object: missing '}'");
        }
    }

    public void parseArray(JsonArray.Builder arrayBuilder) {
        boolean foundClosingBracket = false;

        while (scanner.hasNext()) {
            skipWhitespace();

            if (scanner.hasNext("\\]")) {
                scanner.next();
                foundClosingBracket = true;
                return;
            }

            if (scanner.hasNext("\"")) {
                String strValue = readStringValue();
                if (strValue == null) {
                    arrayBuilder.addError("Unterminated string in array");
                    return;
                }
                arrayBuilder.addElement(strValue.substring(1, strValue.length() - 1));
            } else if (scanner.hasNext("\\{")) {
                scanner.next();
                JsonObject.Builder nestedObjectBuilder = JsonObject.newBuilder().startObject();
                parseNestedObject(nestedObjectBuilder);
                arrayBuilder.addObject(nestedObjectBuilder.build());
            } else if (scanner.hasNext("\\[")) {
                scanner.next();
                JsonArray.Builder nestedArrayBuilder = JsonArray.newBuilder();
                parseArray(nestedArrayBuilder);
                arrayBuilder.addArray(nestedArrayBuilder.build());
            } else if (scanner.hasNext("t")) {
                String value = scanner.findWithinHorizon("true", 4);
                if (value != null && value.equals("true")) {
                    arrayBuilder.addElement(true);
                } else {
                    arrayBuilder.addError("Invalid value in array");
                    return;
                }
            } else if (scanner.hasNext("f")) {
                String value = scanner.findWithinHorizon("false", 5);
                if (value != null && value.equals("false")) {
                    arrayBuilder.addElement(false);
                } else {
                    arrayBuilder.addError("Invalid value in array");
                    return;
                }
            } else if (scanner.hasNext("n")) {
                String value = scanner.findWithinHorizon("null", 4);
                if (value != null && value.equals("null")) {
                    arrayBuilder.addNull();
                } else {
                    arrayBuilder.addError("Invalid value in array");
                    return;
                }
            } else if (scanner.hasNext("-") || scanner.hasNext("[0-9]")) {
                String numValue = scanner.findWithinHorizon("-?[0-9]+(\\.[0-9]+)?", 50);
                if (numValue != null) {
                    arrayBuilder.addElement(Double.parseDouble(numValue));
                } else {
                    arrayBuilder.addError("Invalid number in array");
                    return;
                }
            } else {
                arrayBuilder.addError("Unexpected token in array");
                return;
            }

            skipWhitespace();
            if (scanner.hasNext(",")) {
                scanner.next();
            } else if (scanner.hasNext("\\]")) {
                scanner.next();
                foundClosingBracket = true;
                return;
            } else if (scanner.hasNext()) {
                arrayBuilder.addError("Expected ',' or ']' in array");
                return;
            } else {
                arrayBuilder.addError("Unclosed array: missing ']'");
                return;
            }
        }
        if (!foundClosingBracket) {
            arrayBuilder.addError("Unclosed array: missing ']'");
        }
    }

    private void parseNestedObject(JsonObject.Builder builder) {
        boolean foundClosingBrace = false;

        while (scanner.hasNext()) {
            skipWhitespace();

            if (scanner.hasNext("}")) {
                scanner.next();
                foundClosingBrace = true;
                return;
            }

            if (scanner.hasNext("\"")) {
                String key = readStringValue();
                if (key == null) {
                    builder.addError("Unterminated string in key");
                    return;
                }
                key = key.substring(1, key.length() - 1);

                skipWhitespace();

                if (!scanner.hasNext(":")) {
                    builder.addError("Missing colon after key: " + key);
                    return;
                }
                scanner.next();
                skipWhitespace();

                if (scanner.hasNext("[,}]")) {
                    builder.addError("Missing value for key: " + key);
                    return;
                }

                if (scanner.hasNext("\"")) {
                    String strValue = readStringValue();
                    if (strValue == null) {
                        builder.addError("Unterminated string for key: " + key);
                        return;
                    }
                    builder.addProperty(key, strValue.substring(1, strValue.length() - 1));
                } else if (scanner.hasNext("\\{")) {
                    scanner.next();
                    builder.startObject(key);
                    parseNestedObject(builder);
                    builder.endObject();
                } else if (scanner.hasNext("\\[")) {
                    scanner.next();
                    JsonArray.Builder nestedArrayBuilder = JsonArray.newBuilder().setKey(key);
                    parseArray(nestedArrayBuilder);
                    builder.addArray(nestedArrayBuilder.build());
                } else if (scanner.hasNext("t")) {
                    String value = scanner.findWithinHorizon("true", 4);
                    if (value != null && value.equals("true")) {
                        builder.addProperty(key, true);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("f")) {
                    String value = scanner.findWithinHorizon("false", 5);
                    if (value != null && value.equals("false")) {
                        builder.addProperty(key, false);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("n")) {
                    String value = scanner.findWithinHorizon("null", 4);
                    if (value != null && value.equals("null")) {
                        builder.addNullProperty(key);
                    } else {
                        builder.addError("Invalid value for key: " + key);
                        return;
                    }
                } else if (scanner.hasNext("-") || scanner.hasNext("[0-9]")) {
                    String numValue = scanner.findWithinHorizon("-?[0-9]+(\\.[0-9]+)?", 50);
                    if (numValue != null) {
                        builder.addProperty(key, Double.parseDouble(numValue));
                    } else {
                        builder.addError("Invalid number for key: " + key);
                        return;
                    }
                } else {
                    builder.addError("Expected value after key: " + key);
                    return;
                }

                skipWhitespace();
                if (scanner.hasNext(",")) {
                    scanner.next();
                } else if (scanner.hasNext("}")) {
                    scanner.next();
                    foundClosingBrace = true;
                    return;
                } else if (scanner.hasNext()) {
                    builder.addError("Expected ',' or '}' in object");
                    return;
                } else {
                    builder.addError("Unclosed object: missing '}'");
                    return;
                }
            } else {
                builder.addError("Expected key or closing '}'");
                return;
            }
        }

        if (!foundClosingBrace) {
            builder.addError("Unclosed object: missing '}'");
        }
    }
}
