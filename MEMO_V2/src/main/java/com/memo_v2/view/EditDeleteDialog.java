package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditDeleteDialog extends JDialog {
    private ActivityEntry editedEntry;
    private CSVFile csvFile;
    
    public EditDeleteDialog(Frame owner, ActivityEntry entry, CSVFile file) {
        super(owner, "Edit Entry", true);
        this.editedEntry = entry;
        this.csvFile = file;
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setModal(true);
        
        createUI();
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Timestamp (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Timestamp:"), gbc);
        gbc.gridx = 1;
        JTextField timestampField = new JTextField(editedEntry.getTimestampFormatted(), 20);
        timestampField.setEditable(false);
        formPanel.add(timestampField, gbc);
        
        // Activity Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Activity Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"", "DEV", "TEST", "CEREMONY", "LEARNING",
                          "CONTINUOUS_IMPROVEMENT", "SUPPORT", "ADMIN", "DOCUMENTATION"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setSelectedItem(editedEntry.getActivityType());
        formPanel.add(typeCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(editedEntry.getDescription(), 4, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        String[] statuses = {"", "TODO", "DOING", "DONE", "NOTE"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(editedEntry.getStatus());
        formPanel.add(statusCombo, gbc);
        
        // Comment
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        JTextArea commentArea = new JTextArea(editedEntry.getComment(), 6, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        formPanel.add(commentScroll, gbc);
        
        // Time Spent
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Time (days):"), gbc);
        gbc.gridx = 1;
        JTextField timeField = new JTextField(String.valueOf(editedEntry.getTimeSpentDays()), 10);
        formPanel.add(timeField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            editedEntry.setActivityType((String) typeCombo.getSelectedItem());
            editedEntry.setDescription(descArea.getText());
            editedEntry.setStatus((String) statusCombo.getSelectedItem());
            editedEntry.setComment(commentArea.getText());
            try {
                double time = Double.parseDouble(timeField.getText());
                editedEntry.setTimeSpentDays(time);
                csvFile.saveToFile();
                JOptionPane.showMessageDialog(EditDeleteDialog.this, "Changes saved!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }
}
