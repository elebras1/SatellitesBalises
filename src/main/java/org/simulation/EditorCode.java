package org.simulation;

import org.antlr.generated.BenglemscLexer;
import org.antlr.generated.BenglemscParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.interpreter.BenglemscInterpreter;

import java.awt.*;
import java.awt.event.*;

public class EditorCode extends Frame {

    private final TextArea codeArea;
    private final World world;
    private final BenglemscInterpreter interpreter;

    public EditorCode(World world) {
        super("Éditeur de Code - Simulation");
        this.world = world;
        this.interpreter = new BenglemscInterpreter(this.world);
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
        this.codeArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        this.codeArea.setBackground(new Color(43, 43, 43));
        this.codeArea.setForeground(Color.WHITE);
        this.codeArea.setText(String.join("\n",
                "m1 := new HorizontalMovement(1);",
                "m2 := new HorizontalMovementSatellite(1);",
                "m3 := new SinusMovement(2);",
                "",
                "b1 := new Buoy(64, 2000, 400, 500, m1);",
                "b2 := new Buoy(64, 3000, 300, 450, m3);",
                "s1 := new Satellite(64, 500, 150, m2);",
                "s2 := new Satellite(64, 550, 140, m2);",
                "",
                "b1.start();",
                "b2.start();",
                "s1.start();",
                "s2.start();"
        ));

        this.add(this.codeArea, BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Button runButton = new Button("Exécuter");
        Button clearButton = new Button("Effacer");

        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> this.runCode());
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
        System.out.println("<UNK>diteur de code");
        String code = this.codeArea.getText().trim();

        if (code.isEmpty()) {
            return;
        }

        try {
            CharStream input = CharStreams.fromString(code);
            BenglemscLexer lexer = new BenglemscLexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            BenglemscParser parser = new BenglemscParser(tokens);

            BenglemscParser.ProgramContext tree = parser.program();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                return;
            }

            interpreter.visit(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}