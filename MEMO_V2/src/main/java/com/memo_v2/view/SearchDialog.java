package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchDialog extends JDialog {
    private JTextField timestampField;
    private JComboBox<String> activityTypeCombo;
    private JTextField descriptionField;
    private JComboBox<String> statusCombo;
    private JTextField commentField;
    private JRadioButton andRadio, orRadio;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;

    public SearchDialog(Frame owner) {
        super(owner, "Search Activity Entries", true);
        setSize(900, 700);
        setLocationRelativeTo(owner);
        setModal(true);

        createUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search criteria panel
        JPanel searchPanel = createSearchCriteriaPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Results table
        resultsTableModel = new DefaultTableModel(
            new Object[]{"File", "Timestamp", "Type", "Description", "Status", "Comment", "Time (days)"}, 0);
        resultsTable = new JTable(resultsTableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            resultsTableModel.setRowCount(0);
            timestampField.setText("");
            descriptionField.setText("");
            commentField.setText("");
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createSearchCriteriaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Timestamp
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Timestamp:"), gbc);
        gbc.gridx = 1;
        timestampField = new JTextField(20);
        panel.add(timestampField, gbc);

        // Activity Type
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Activity Type:"), gbc);
        gbc.gridx = 1;
        String[] activityTypes = {"", "DEV", "TEST", "CEREMONY", "LEARNING",
                                  "CONTINUOUS_IMPROVEMENT", "SUPPORT", "ADMIN", "DOCUMENTATION"};
        activityTypeCombo = new JComboBox<>(activityTypes);
        panel.add(activityTypeCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(45);
        panel.add(descriptionField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        String[] statuses = {"", "TODO", "DOING", "DONE", "NOTE"};
        statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo, gbc);

        // Comment
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        commentField = new JTextField(45);
        panel.add(commentField, gbc);

        // AND/OR logic
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        ButtonGroup group = new ButtonGroup();
        andRadio = new JRadioButton("AND (all conditions must match)", true);
        orRadio = new JRadioButton("OR (any condition can match)");
        group.add(andRadio);
        group.add(orRadio);
        JPanel logicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logicPanel.add(andRadio);
        logicPanel.add(orRadio);
        panel.add(logicPanel, gbc);

        return panel;
    }

    private void performSearch() {
        resultsTableModel.setRowCount(0);
        int matchCount = 0;
        double totalTime = 0.0;

        // Get all CSV files
        List<CSVFile> allFiles = new ArrayList<>();
        File storageDir = new File("./log");
        if (storageDir.exists()) {
            for (File f : storageDir.listFiles((d, n) -> n.matches(".*_tracking_\\d{8}\\.csv"))) {
                try {
                    CSVFile csvFile = new CSVFile(f.getAbsolutePath());
                    csvFile.loadFromFile();
                    allFiles.add(csvFile);
                } catch (Exception e) {
                    // Skip unreadable files
                }
            }
        }

        for (CSVFile file : allFiles) {
            for (ActivityEntry entry : file.getEntries()) {
                boolean matches = checkMatch(entry);
                if (matches) {
                    matchCount++;
                    totalTime += entry.getTimeSpentDays();
                    Object[] row = {
                        file.getProjectName(),
                        entry.getTimestampFormatted(),
                        entry.getActivityType(),
                        entry.getDescription(),
                        entry.getStatus(),
                        entry.getComment(),
                        String.format("%.3f", entry.getTimeSpentDays())
                    };
                    resultsTableModel.addRow(row);
                }
            }
        }

        // Show summary
        if (matchCount > 0) {
            JOptionPane.showMessageDialog(this,
                String.format("Found %d matching entries.\nTotal time: %.3f days (%.2f hours)",
                    matchCount, totalTime, totalTime * 24),
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No matches found.",
                "Search Results", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean checkMatch(ActivityEntry entry) {
        String ts = timestampField.getText().trim();
        String type = (String) activityTypeCombo.getSelectedItem();
        String desc = descriptionField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String comment = commentField.getText().trim();

        boolean tsMatch = ts.isEmpty() || entry.getTimestampFormatted().contains(ts);
        boolean typeMatch = type.isEmpty() || entry.getActivityType().equals(type);
        boolean descMatch = desc.isEmpty() || entry.getDescription().toLowerCase().contains(desc.toLowerCase());
        boolean statusMatch = status.isEmpty() || entry.getStatus().equals(status);
        boolean commentMatch = comment.isEmpty() || entry.getComment().toLowerCase().contains(comment.toLowerCase());

        if (andRadio.isSelected()) {
            return tsMatch && typeMatch && descMatch && statusMatch && commentMatch;
        } else {
            return tsMatch || typeMatch || descMatch || statusMatch || commentMatch;
        }
    }
}
