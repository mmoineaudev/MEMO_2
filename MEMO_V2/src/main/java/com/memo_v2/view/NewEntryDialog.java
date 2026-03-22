package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import com.memo_v2.model.ActivityTracker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewEntryDialog extends JDialog {
    private JTextField timestampField;
    private JComboBox<String> activityTypeCombo;
    private JTextField descriptionField;
    private JTextArea suggestionsTextArea;
    private JComboBox<String> statusCombo;
    private JTextArea commentArea;
    private JTextField timeSpentField;
    
    public NewEntryDialog(Frame owner, CSVFile currentFile) {
        super(owner, "New Activity Entry", true);
        setSize(700, 600);
        setLocationRelativeTo(owner);
        setModal(true);
        
        createUI(currentFile);
    }
    
    private void createUI(CSVFile currentFile) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Timestamp
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Timestamp:"), gbc);
        gbc.gridx = 1;
        timestampField = new JTextField(LocalDateTime.now().toString(), 25);
        mainPanel.add(timestampField, gbc);
        
        // Activity Type
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Activity Type:"), gbc);
        gbc.gridx = 1;
        String[] activityTypes = {"DEV", "TEST", "CEREMONY", "LEARNING", 
                                  "CONTINUOUS_IMPROVEMENT", "SUPPORT", "ADMIN", "DOCUMENTATION"};
        activityTypeCombo = new JComboBox<>(activityTypes);
        mainPanel.add(activityTypeCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(45);
        mainPanel.add(descriptionField, gbc);
        
        // Recent suggestions panel
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Recent Descriptions (click to copy):"), gbc);
        gbc.gridx = 1;
        suggestionsTextArea = new JTextArea(5, 45);
        suggestionsTextArea.setEditable(false);
        suggestionsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        suggestionsTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionsTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int line = suggestionsTextArea.getCaretPosition();
                String text = suggestionsTextArea.getText();
                String[] lines = text.split("\n");
                if (line >= 0 && line < lines.length) {
                    descriptionField.setText(lines[line]);
                }
            }
        });
        JScrollPane suggestionScroll = new JScrollPane(suggestionsTextArea);
        mainPanel.add(suggestionScroll, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        String[] statuses = {"TODO", "DOING", "DONE", "NOTE"};
        statusCombo = new JComboBox<>(statuses);
        mainPanel.add(statusCombo, gbc);
        
        // Comment
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        commentArea = new JTextArea(5, 45);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        mainPanel.add(commentScroll, gbc);
        
        // Time Spent
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Time (proportional days):"), gbc);
        gbc.gridx = 1;
        timeSpentField = new JTextField("0.125", 10);
        mainPanel.add(timeSpentField, gbc);
        
        // Helper text
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        JLabel helper = new JLabel("Examples: 1h=0.125, 1h20=0.2, 1h45=0.25, 7h=1.0");
        mainPanel.add(helper, gbc);
        gbc.gridwidth = 1;
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (validateAndSave(currentFile)) {
                dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        setContentPane(mainPanel);
        loadSuggestions();
    }
    
    private void loadSuggestions() {
        ActivityTracker tracker = new ActivityTracker();
        List<String> descriptions = tracker.getLastDistinctDescriptions(10);
        StringBuilder sb = new StringBuilder();
        for (String desc : descriptions) {
            sb.append(desc).append("\n");
        }
        suggestionsTextArea.setText(sb.toString());
    }
    
    private boolean validateAndSave(CSVFile currentFile) {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required", "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double timeSpent = Double.parseDouble(timeSpentField.getText());
            if (timeSpent < 0) {
                throw new IllegalArgumentException("Time cannot be negative");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use decimal days.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Create entry
        ActivityEntry entry = new ActivityEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setActivityType((String) activityTypeCombo.getSelectedItem());
        entry.setDescription(description);
        entry.setStatus((String) statusCombo.getSelectedItem());
        entry.setComment(commentArea.getText());
        entry.setTimeSpentDays(Double.parseDouble(timeSpentField.getText()));
        
        // Add to current file
        currentFile.getEntries().add(entry);
        
        try {
            currentFile.saveToFile();
            JOptionPane.showMessageDialog(this, "Entry saved successfully!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving entry: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}
