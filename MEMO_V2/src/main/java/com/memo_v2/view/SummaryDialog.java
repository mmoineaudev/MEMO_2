package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryDialog extends JDialog {
    private JComboBox<String> summaryTypeCombo;
    private JTextArea resultsTextArea;
    private CSVFile currentFile;
    private String initialSummaryType = "Daily";

    public SummaryDialog(Frame owner, CSVFile currentFile) {
        super(owner, "Activity Summary", true);
        this.currentFile = currentFile;
        setSize(700, 600);
        setLocationRelativeTo(owner);
        setModal(true);

        createUI();
    }

    public void setInitialSummaryType(String type) {
        this.initialSummaryType = type;
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Summary Type:"));
        String[] types = {"Daily", "Weekly"};
        summaryTypeCombo = new JComboBox<>(types);
        int initialIndex = "Weekly".equals(initialSummaryType) ? 1 : 0;
        summaryTypeCombo.setSelectedIndex(initialIndex);
        topPanel.add(summaryTypeCombo);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Results
        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> generateSummary());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void generateSummary() {
        String type = (String) summaryTypeCombo.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        if ("Daily".equals(type)) {
            sb.append(generateDailySummary());
        } else {
            sb.append(generateWeeklySummary());
        }

        resultsTextArea.setText(sb.toString());
    }

    private String generateDailySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("DAILY ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Group by date, then by description within each date
        Map<LocalDate, Map<String, Double>> dailyByDesc = new HashMap<>();

        for (ActivityEntry entry : currentFile.getEntries()) {
            LocalDate date = entry.getTimestamp().toLocalDate();
            String desc = entry.getDescription();
            double time = entry.getTimeSpentDays();
            
            Map<String, Double> dayMap = dailyByDesc.computeIfAbsent(date, k -> new HashMap<>());
            dayMap.put(desc, dayMap.getOrDefault(desc, 0.0) + time);
        }

        for (LocalDate date : dailyByDesc.keySet()) {
            sb.append("\n").append(date).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = dailyByDesc.get(date);
            
            // Sort descriptions alphabetically
            List<String> sortedDeps = new ArrayList<>(descMap.keySet());
            Collections.sort(sortedDeps);
            
            for (String desc : sortedDeps) {
                double timeDays = descMap.get(desc);
                sb.append("  ").append(desc).append(": ");
                sb.append(String.format("%.3f days (%.2f hours)\n", timeDays, timeDays * 7.75));
                total += timeDays;
            }
            
            sb.append("  Total: ").append(String.format("%.3f days (%.2f hours)", total, total * 7.75)).append("\n");
        }

        return sb.toString();
    }

    private String generateWeeklySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("WEEKLY ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Group by week, then by description within each week
        Map<String, Map<String, Double>> weeklyByDesc = new HashMap<>();

        for (ActivityEntry entry : currentFile.getEntries()) {
            LocalDate date = entry.getTimestamp().toLocalDate();
            String desc = entry.getDescription();
            double time = entry.getTimeSpentDays();
            int weekNum = (date.getDayOfYear() - 1) / 7 + 1;
            String weekKey = date.getYear() + "-W" + String.format("%02d", weekNum);
            
            Map<String, Double> weekMap = weeklyByDesc.computeIfAbsent(weekKey, k -> new HashMap<>());
            weekMap.put(desc, weekMap.getOrDefault(desc, 0.0) + time);
        }

        for (String weekKey : weeklyByDesc.keySet()) {
            sb.append("\n").append(weekKey).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = weeklyByDesc.get(weekKey);
            
            // Sort descriptions alphabetically
            List<String> sortedDeps = new ArrayList<>(descMap.keySet());
            Collections.sort(sortedDeps);
            
            for (String desc : sortedDeps) {
                double timeDays = descMap.get(desc);
                sb.append("  ").append(desc).append(": ");
                sb.append(String.format("%.3f days (%.2f hours)\n", timeDays, timeDays * 7.75));
                total += timeDays;
            }
            
            sb.append("  Total: ").append(String.format("%.3f days (%.2f hours)", total, total * 7.75)).append("\n");
        }

        return sb.toString();
    }
}
