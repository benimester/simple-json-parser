package edu.bbte.simple_json_parser.view;

import edu.bbte.simple_json_parser.JsonParser;
import edu.bbte.simple_json_parser.types.JsonObject;
import edu.bbte.simple_json_parser.visitor.JsonFormatterVisitor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JsonFormatterApp extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JLabel statusLabel;

    public JsonFormatterApp() {
        setTitle("JSON Formatter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create the split panel for input and output
        JPanel textAreasPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Input panel (left)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("Input JSON:");
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // Output panel (right)
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel outputLabel = new JLabel("Formatted Output:");
        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        textAreasPanel.add(inputPanel);
        textAreasPanel.add(outputPanel);

        // Bottom panel with button and status
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // Format button (centered)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton formatButton = new JButton("Format");
        formatButton.setPreferredSize(new Dimension(100, 30));
        formatButton.addActionListener(e -> formatJson());
        buttonPanel.add(formatButton);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(textAreasPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void formatJson() {
        String input = inputTextArea.getText().trim();

        if (input.isEmpty()) {
            statusLabel.setText("Please enter JSON to format");
            statusLabel.setForeground(Color.ORANGE.darker());
            outputTextArea.setText("");
            return;
        }

        try {
            JsonParser jsonParser = new JsonParser(input);
            JsonObject result = jsonParser.startReading();

            JsonFormatterVisitor visitor = new JsonFormatterVisitor();
            result.accept(visitor);
            String formattedOutput = visitor.getResult();

            outputTextArea.setText(formattedOutput);

            // Check if output contains error markers
            if (formattedOutput.contains("/* Error:")) {
                statusLabel.setText("JSON parsed with errors");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("JSON parsed successfully");
                statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            outputTextArea.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JsonFormatterApp app = new JsonFormatterApp();
            app.setVisible(true);
        });
    }
}

