package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExportDialog extends JDialog {
    private List<ActivityEntry> exportEntries = new ArrayList<>();
    private String selectedProject;
    
    public ExportDialog(Frame owner, CSVFile file) {
        super(owner, "Export Entries", true);
        this.exportEntries = file.getEntries();
        this.selectedProject = file.getProjectName();
        setSize(600, 450);
        setLocationRelativeTo(owner);
        setModal(true);
        
        createUI();
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        String[] columns = {"Timestamp", "Type", "Description", "Status", "Comment", "Time (days)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (ActivityEntry entry : exportEntries) {
            Object[] row = {
                entry.getTimestampFormatted(),
                entry.getActivityType(),
                entry.getDescription(),
                entry.getStatus(),
                entry.getComment(),
                String.format("%.3f", entry.getTimeSpentDays())
            };
            model.addRow(row);
        }
        JTable previewTable = new JTable(model);
        previewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        previewTable.setRowSelectionAllowed(true);
        JScrollPane scrollPane = new JScrollPane(previewTable);
        previewPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Stats
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int totalEntries = exportEntries.size();
        double totalTimeDays = 0;
        for (ActivityEntry entry : exportEntries) {
            totalTimeDays += entry.getTimeSpentDays();
        }
        double totalTimeHours = totalTimeDays * 7.75;
        statsPanel.add(new JLabel(String.format("Total entries: %d | Total time: %.3f days (%.2f hours)", 
            totalEntries, totalTimeDays, totalTimeHours)));
        previewPanel.add(statsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        
        // Export options
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Format selection
        gbc.gridx = 0; gbc.gridy = 0;
        optionsPanel.add(new JLabel("Format:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"CSV", "Text Report"});
        formatCombo.setSelectedIndex(1); // Default to Text Report
        optionsPanel.add(formatCombo, gbc);
        
        // Filename
        gbc.gridx = 0; gbc.gridy = 1;
        optionsPanel.add(new JLabel("Filename:"), gbc);
        gbc.gridx = 1;
        JTextField filenameField = new JTextField(selectedProject + "_export.txt", 20);
        optionsPanel.add(filenameField, gbc);
        
        mainPanel.add(optionsPanel, BorderLayout.NORTH);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            String filename = filenameField.getText();
            String format = (String) formatCombo.getSelectedItem();
            try {
                if (format.equals("CSV")) {
                    exportToCSV(filename);
                } else {
                    exportToText(filename);
                }
                JOptionPane.showMessageDialog(ExportDialog.this, 
                    "Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(exportButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }
    
    private void exportToCSV(String filename) throws IOException {
        if (!filename.endsWith(".csv")) {
            filename += ".csv";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Header
            writer.write("timestamp;activity_type;description;status;comment;time_spent_days");
            writer.newLine();
            
            // Data rows
            for (ActivityEntry entry : exportEntries) {
                writer.write(String.format("%s;%s;%s;%s;%s;%.6f",
                    entry.getTimestampFormatted(),
                    entry.getActivityType(),
                    entry.getDescription().replace("\n", " "),
                    entry.getStatus(),
                    entry.getComment().replace("\n", " "),
                    entry.getTimeSpentDays()));
                writer.newLine();
            }
        }
    }
    
    private void exportToText(String filename) throws IOException {
        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Header
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write(String.format("Export: %s", selectedProject));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            
            // Summary
            int totalEntries = exportEntries.size();
            double totalTimeDays = 0;
            for (ActivityEntry entry : exportEntries) {
                totalTimeDays += entry.getTimeSpentDays();
            }
            double totalTimeHours = totalTimeDays * 7.75;
            
            writer.newLine();
            writer.write(String.format("Total entries: %d", totalEntries));
            writer.newLine();
            writer.write(String.format("Total time: %.3f days (%.2f hours)", 
                totalTimeDays, totalTimeHours));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();
            
            // Entries
            for (ActivityEntry entry : exportEntries) {
                writer.write(String.format("[%s] %s", 
                    entry.getTimestampFormatted(), entry.getActivityType()));
                writer.newLine();
                writer.write(String.format("  Description: %s", entry.getDescription()));
                writer.newLine();
                writer.write(String.format("  Status: %s", entry.getStatus()));
                writer.newLine();
                writer.write(String.format("  Comment: %s", entry.getComment()));
                writer.newLine();
                writer.write(String.format("  Time: %.3f days (%.2f hours)", 
                    entry.getTimeSpentDays(), entry.getTimeSpentDays() * 7.75));
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }
        }
    }
}
