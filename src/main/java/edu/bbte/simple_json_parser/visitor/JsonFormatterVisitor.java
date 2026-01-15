package edu.bbte.simple_json_parser.visitor;

import edu.bbte.simple_json_parser.types.*;

import java.util.List;

/**
 * Concrete Visitor implementation that formats JSON nodes into a pretty-printed string.
 */
public class JsonFormatterVisitor implements JsonVisitor {
    private final StringBuilder sb = new StringBuilder();
    private int indentLevel = 0;
    private static final String INDENT = "  ";

    private void appendIndent() {
        for (int i = 0; i < indentLevel; i++) {
            sb.append(INDENT);
        }
    }

    public String getResult() {
        return sb.toString();
    }

    @Override
    public void visit(JsonObject jsonObject) {
        String key = jsonObject.getKey();
        List<JsonNode> children = jsonObject.getChildren();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": {\n");
        } else if (indentLevel > 0) {
            appendIndent();
            sb.append("{\n");
        } else {
            sb.append("{\n");
        }

        indentLevel++;
        for (int i = 0; i < children.size(); i++) {
            JsonNode child = children.get(i);
            child.accept(this);
            if (i < children.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        indentLevel--;

        appendIndent();
        sb.append("}");
    }

    @Override
    public void visit(JsonArray jsonArray) {
        String key = jsonArray.getKey();
        List<JsonNode> elements = jsonArray.getElements();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": [\n");
        } else {
            appendIndent();
            sb.append("[\n");
        }

        indentLevel++;
        for (int i = 0; i < elements.size(); i++) {
            JsonNode element = elements.get(i);
            if (!(element instanceof JsonObject) && !(element instanceof JsonArray)) {
                appendIndent();
            }
            element.accept(this);
            if (i < elements.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        indentLevel--;

        appendIndent();
        sb.append("]");
    }

    @Override
    public void visit(JsonString jsonString) {
        String key = jsonString.getKey();
        String value = jsonString.getValue();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": ");
        }
        sb.append("\"").append(escapeString(value)).append("\"");
    }

    @Override
    public void visit(JsonNumber jsonNumber) {
        String key = jsonNumber.getKey();
        double value = jsonNumber.getValue();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": ");
        }

        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            sb.append((long) value);
        } else {
            sb.append(value);
        }
    }

    @Override
    public void visit(JsonBoolean jsonBoolean) {
        String key = jsonBoolean.getKey();
        boolean value = jsonBoolean.getValue();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": ");
        }
        sb.append(value);
    }

    @Override
    public void visit(JsonNull jsonNull) {
        String key = jsonNull.getKey();

        if (key != null) {
            appendIndent();
            sb.append("\"").append(key).append("\": ");
        }
        sb.append("null");
    }

    @Override
    public void visit(ErrorNode errorNode) {
        appendIndent();
        sb.append("/* Error: ").append(errorNode.getError()).append(" */");
    }

    private String escapeString(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\b': result.append("\\b"); break;
                case '\f': result.append("\\f"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                default: result.append(c);
            }
        }
        return result.toString();
    }
}

