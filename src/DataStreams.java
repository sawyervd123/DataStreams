import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreams extends JFrame {
    private final JTextArea fullTextArea;
    private final JTextArea filteredTextArea;
    private final JTextField searchField;
    private Path currentFilePath;
    private List<String> fullTextLines;

    public DataStreams() {
        setTitle("Java Data Streams Search");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        JButton loadButton = new JButton("Load File");
        JButton searchButton = new JButton("Search");
        JButton quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        add(topPanel, BorderLayout.NORTH);

        fullTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        fullTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane fullScroll = new JScrollPane(fullTextArea);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fullScroll, filteredScroll);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFilePath = fileChooser.getSelectedFile().toPath();
            try (Stream<String> lines = Files.lines(currentFilePath)) {
                fullTextLines = lines.collect(Collectors.toList());
                fullTextArea.setText(String.join("\n", fullTextLines));
                filteredTextArea.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private void searchFile(ActionEvent e) {
        if (currentFilePath == null || fullTextLines == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first.");
            return;
        }

        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.");
            return;
        }

        try (Stream<String> lines = fullTextLines.stream()) {
            List<String> result = lines.filter(line -> line.toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            filteredTextArea.setText(String.join("\n", result));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreams::new);
    }
}
