package org.simulation;

import java.awt.*;
import java.awt.event.*;

public class EditorCode extends Frame {

    private final TextArea codeArea;

    public EditorCode() {
        super("Éditeur de Code - Simulation");
        this.setSize(900, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));

        Label titleLabel = new Label("Éditeur de code", Label.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        Panel titlePanel = new Panel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        titlePanel.add(new Canvas() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 10);
            }
        }, BorderLayout.NORTH);
        this.add(titlePanel, BorderLayout.NORTH);

        this.codeArea = new TextArea();
        this.codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        this.codeArea.setBackground(new Color(43, 43, 43));
        this.codeArea.setForeground(Color.WHITE);

        this.add(this.codeArea, BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Button runButton = new Button("Exécuter");
        Button clearButton = new Button("Effacer");

        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> runCode());
        clearButton.addActionListener(e -> this.codeArea.setText(""));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.setVisible(true);
    }

    private void runCode() {
        String code = this.codeArea.getText().trim();
    }

}