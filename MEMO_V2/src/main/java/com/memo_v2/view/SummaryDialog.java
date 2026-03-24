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
    private JComboBox<String> dateRangeCombo;
    private JTextArea resultsTextArea;
    private List<CSVFile> allFiles;
    private CSVFile currentFile;
    private String initialSummaryType = "Timeframe";

    public SummaryDialog(Frame owner, CSVFile currentFile, List<CSVFile> allFiles) {
        super(owner, "Activity Summary", true);
        this.currentFile = currentFile;
        this.allFiles = allFiles;
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
        String[] types = {"Daily", "Monthly", "Timeframe"};
        summaryTypeCombo = new JComboBox<>(types);
        int initialIndex = 0;
        if ("Monthly".equals(initialSummaryType)) initialIndex = 1;
        if ("Timeframe".equals(initialSummaryType)) initialIndex = 2;
        summaryTypeCombo.setSelectedIndex(initialIndex);
        topPanel.add(summaryTypeCombo);

        // Date range selector (hidden by default, shown for Timeframe)
        dateRangeCombo = new JComboBox<>();
        dateRangeCombo.addActionListener(e -> {
            if ("Timeframe".equals(summaryTypeCombo.getSelectedItem())) {
                dateRangeCombo.setVisible(true);
                dateRangeCombo.setEnabled(true);
            } else {
                dateRangeCombo.setVisible(false);
                dateRangeCombo.setEnabled(false);
            }
        });
        topPanel.add(dateRangeCombo);
        dateRangeCombo.setVisible(false);
        dateRangeCombo.setEnabled(false);
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
        } else if ("Monthly".equals(type)) {
            sb.append(generateMonthlySummary());
        } else {
            sb.append(generateTimeframeSummary());
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

    private String generateMonthlySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("MONTHLY ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Group by month, then by description within each month
        Map<String, Map<String, Double>> monthlyByDesc = new HashMap<>();

        for (CSVFile file : allFiles) {
            for (ActivityEntry entry : file.getEntries()) {
                LocalDate date = entry.getTimestamp().toLocalDate();
                String desc = entry.getDescription();
                double time = entry.getTimeSpentDays();

                // Use year-month key (e.g., "2024-03")
                String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue());

                Map<String, Double> monthMap = monthlyByDesc.computeIfAbsent(monthKey, k -> new HashMap<>());
                monthMap.put(desc, monthMap.getOrDefault(desc, 0.0) + time);
            }
        }

        for (String monthKey : monthlyByDesc.keySet()) {
            sb.append("\n").append(monthKey).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = monthlyByDesc.get(monthKey);

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

    private String generateTimeframeSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("TIMEFRAME ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Determine the date range from the selected option
        int selectedIndex = dateRangeCombo.getSelectedIndex();
        LocalDate startDate, endDate;

        switch (selectedIndex) {
            case 0: // Last 7 days
                endDate = LocalDate.now();
                startDate = endDate.minusDays(7);
                sb.append("Range: ").append(startDate).append(" to ").append(endDate).append("\n\n");
                break;
            case 1: // Last 14 days
                endDate = LocalDate.now();
                startDate = endDate.minusDays(14);
                sb.append("Range: ").append(startDate).append(" to ").append(endDate).append("\n\n");
                break;
            case 2: // Last 30 days
                endDate = LocalDate.now();
                startDate = endDate.minusDays(30);
                sb.append("Range: ").append(startDate).append(" to ").append(endDate).append("\n\n");
                break;
            case 3: // Current week
                LocalDate today = LocalDate.now();
                LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                startDate = startOfWeek;
                endDate = startOfWeek.plusDays(6);
                sb.append("Range: ").append(startDate).append(" to ").append(endDate).append("\n\n");
                break;
            case 4: // Current month
                endDate = LocalDate.now();
                startDate = endDate.withDayOfMonth(1);
                sb.append("Range: ").append(startDate).append(" to ").append(endDate).append("\n\n");
                break;
            default: // All time
                sb.append("Range: All entries\n\n");
                startDate = LocalDate.MIN;
                endDate = LocalDate.MAX;
        }

        // Group by date, then by description within each date, filtering by timeframe
        Map<LocalDate, Map<String, Double>> dailyByDesc = new HashMap<>();

        for (CSVFile file : allFiles) {
            for (ActivityEntry entry : file.getEntries()) {
                LocalDate date = entry.getTimestamp().toLocalDate();
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    String desc = entry.getDescription();
                    double time = entry.getTimeSpentDays();

                    Map<String, Double> dayMap = dailyByDesc.computeIfAbsent(date, k -> new HashMap<>());
                    dayMap.put(desc, dayMap.getOrDefault(desc, 0.0) + time);
                }
            }
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
}
