package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummaryDialog extends JDialog {
    private JComboBox<String> summaryTypeCombo;
    private JTextArea resultsTextArea;
    private java.util.List<CSVFile> allFiles;
    private CSVFile currentFile;
    private final LocalDate filterStartDate;
    private final LocalDate filterEndDate;
    
    public SummaryDialog(Frame owner, CSVFile currentFile, java.util.List<CSVFile> allFiles, 
                         LocalDate filterStartDate, LocalDate filterEndDate) {
        super(owner, "Activity Summary", true);
        this.currentFile = currentFile;
        this.allFiles = allFiles;
        this.filterStartDate = filterStartDate;
        this.filterEndDate = filterEndDate;
        setSize(750, 650);
        setLocationRelativeTo(owner);
        setModal(true);
        createUI();
    }

   private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Summary type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(new JLabel("Summary Type:"));
        String[] types = {"Daily", "Weekly", "Monthly"};
        summaryTypeCombo = new JComboBox<>(types);
        topPanel.add(summaryTypeCombo);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Date range info label (read-only, showing global filter)
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Active Filter", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION
        ));
        
        String filterText;
        if (filterStartDate == null && filterEndDate == null) {
            filterText = "No filter - all dates";
        } else if (filterStartDate != null && filterEndDate == null) {
            filterText = "From " + filterStartDate;
        } else if (filterStartDate == null && filterEndDate != null) {
            filterText = "To " + filterEndDate;
        } else {
            filterText = filterStartDate + " to " + filterEndDate;
        }
        
        JLabel filterLabel = new JLabel(filterText);
        filterLabel.setForeground(Color.GRAY);
        datePanel.add(filterLabel);
        mainPanel.add(datePanel, BorderLayout.CENTER);
        
        // Results
        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Summary Results"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate Summary");
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
        } else if ("Weekly".equals(type)) {
            sb.append(generateWeeklySummary());
        } else {
            sb.append(generateMonthlySummary());
        }

        resultsTextArea.setText(sb.toString());
    }

    /**
     * Generate daily summary grouped by date.
     * Respects date range filter if set.
     */
    private String generateDailySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("DAILY ACTIVITY SUMMARY\n");
        sb.append("Date Range: ").append(
            filterStartDate != null && filterEndDate != null 
                ? filterStartDate + " to " + filterEndDate 
                : "All dates"
        ).append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        // Collect all entries from all files
        java.util.List<ActivityEntry> allEntries = new java.util.ArrayList<>();
        for (CSVFile file : allFiles) {
            allEntries.addAll(file.getEntries());
        }

        // Apply date range filter
        if (filterStartDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isBefore(filterStartDate));
        }
        if (filterEndDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isAfter(filterEndDate));
        }

        // Group by date
        Map<LocalDate, Map<String, Double>> dailyByDesc = new LinkedHashMap<>();
        for (ActivityEntry entry : allEntries) {
            LocalDate date = entry.getTimestamp().toLocalDate();
            String desc = entry.getDescription();
            double time = entry.getTimeSpentDays();
            
            dailyByDesc.computeIfAbsent(date, k -> new HashMap<>())
                .merge(desc, time, Double::sum);
        }

        // Sort dates
        java.util.List<LocalDate> sortedDates = new java.util.ArrayList<>(dailyByDesc.keySet());
        Collections.sort(sortedDates);

        // Generate output
        for (LocalDate date : sortedDates) {
            sb.append("\n").append(date).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = dailyByDesc.get(date);
            
            List<String> sortedDeps = new ArrayList<>(descMap.keySet());
            Collections.sort(sortedDeps);
            
            for (String desc : sortedDeps) {
                double timeDays = descMap.get(desc);
                sb.append("  ").append(String.format("%-30s", desc))
                  .append(": ").append(String.format("%.3f days (%.1f hours)\n", timeDays, timeDays * 7.75));
                total += timeDays;
            }
            
            sb.append("  ").append("=".repeat(35)).append("\n");
            sb.append("  Total: ").append(String.format("%.3f days (%.1f hours)\n", total, total * 7.75));
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        return sb.toString();
    }

    /**
     * Generate weekly summary grouped by week.
     * Respects date range filter if set.
     */
    private String generateWeeklySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("WEEKLY ACTIVITY SUMMARY\n");
        sb.append("Date Range: ").append(
            filterStartDate != null && filterEndDate != null 
                ? filterStartDate + " to " + filterEndDate 
                : "All dates"
        ).append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        // Collect all entries from all files
        java.util.List<ActivityEntry> allEntries = new java.util.ArrayList<>();
        for (CSVFile file : allFiles) {
            allEntries.addAll(file.getEntries());
        }

        // Apply date range filter
        if (filterStartDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isBefore(filterStartDate));
        }
        if (filterEndDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isAfter(filterEndDate));
        }

        // Group by week (ISO week year and week number)
        Map<String, Map<String, Double>> weeklyByDesc = new LinkedHashMap<>();
        for (ActivityEntry entry : allEntries) {
            LocalDate date = entry.getTimestamp().toLocalDate();
            int weekYear = date.getYear();
            int weekNum = (date.getDayOfYear() - 1) / 7 + 1;
            String weekKey = weekYear + "-W" + String.format("%02d", weekNum);
            String desc = entry.getDescription();
            double time = entry.getTimeSpentDays();
            
            weeklyByDesc.computeIfAbsent(weekKey, k -> new HashMap<>())
                .merge(desc, time, Double::sum);
        }

        // Sort weeks
        java.util.List<String> sortedWeeks = new java.util.ArrayList<>(weeklyByDesc.keySet());
        Collections.sort(sortedWeeks);

        // Generate output
        for (String weekKey : sortedWeeks) {
            sb.append("\n").append(weekKey).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = weeklyByDesc.get(weekKey);
            
            java.util.List<String> sortedDeps = new java.util.ArrayList<>(descMap.keySet());
            Collections.sort(sortedDeps);
            
            for (String desc : sortedDeps) {
                double timeDays = descMap.get(desc);
                sb.append("  ").append(String.format("%-30s", desc))
                  .append(": ").append(String.format("%.3f days (%.1f hours)\n", timeDays, timeDays * 7.75));
                total += timeDays;
            }
            
            sb.append("  ").append("=".repeat(35)).append("\n");
            sb.append("  Total: ").append(String.format("%.3f days (%.1f hours)\n", total, total * 7.75));
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        return sb.toString();
    }

    /**
     * Generate monthly summary grouped by month.
     * Respects date range filter if set.
     */
    private String generateMonthlySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("MONTHLY ACTIVITY SUMMARY\n");
        sb.append("Date Range: ").append(
            filterStartDate != null && filterEndDate != null 
                ? filterStartDate + " to " + filterEndDate 
                : "All dates"
        ).append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        // Collect all entries from all files
        java.util.List<ActivityEntry> allEntries = new java.util.ArrayList<>();
        for (CSVFile file : allFiles) {
            allEntries.addAll(file.getEntries());
        }

        // Apply date range filter
        if (filterStartDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isBefore(filterStartDate));
        }
        if (filterEndDate != null) {
            allEntries.removeIf(e -> e.getTimestamp().toLocalDate().isAfter(filterEndDate));
        }

        // Group by month (YYYY-MM format)
        Map<String, Map<String, Double>> monthlyByDesc = new LinkedHashMap<>();
        for (ActivityEntry entry : allEntries) {
            LocalDate date = entry.getTimestamp().toLocalDate();
            String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            String desc = entry.getDescription();
            double time = entry.getTimeSpentDays();
            
            monthlyByDesc.computeIfAbsent(monthKey, k -> new HashMap<>())
                .merge(desc, time, Double::sum);
        }

         // Sort months
        java.util.List<String> sortedMonths = new java.util.ArrayList<>(monthlyByDesc.keySet());
        Collections.sort(sortedMonths);

        // Generate output
        for (String monthKey : sortedMonths) {
            sb.append("\n").append(monthKey).append(":\n");
            double total = 0.0;
            Map<String, Double> descMap = monthlyByDesc.get(monthKey);
            
            java.util.List<String> sortedDeps = new java.util.ArrayList<>(descMap.keySet());
            Collections.sort(sortedDeps);
            
            for (String desc : sortedDeps) {
                double timeDays = descMap.get(desc);
                sb.append("  ").append(String.format("%-30s", desc))
                  .append(": ").append(String.format("%.3f days (%.1f hours)\n", timeDays, timeDays * 7.75));
                total += timeDays;
            }
            
            sb.append("  ").append("=".repeat(35)).append("\n");
            sb.append("  Total: ").append(String.format("%.3f days (%.1f hours)\n", total, total * 7.75));
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        return sb.toString();
    }
}
