package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class SummaryDialog extends JDialog {
    private JComboBox<String> summaryTypeCombo;
    private JComboBox<String> dateRangeCombo;
    private JTextArea resultsTextArea;
    private List<CSVFile> allFiles;
    private CSVFile currentFile;
    private boolean filterActive = false;

    public SummaryDialog(Frame owner, CSVFile currentFile, List<CSVFile> allFiles) {
        super(owner, "Activity Summary", true);
        this.currentFile = currentFile;
        this.allFiles = allFiles;
        setSize(700, 600);
        setLocationRelativeTo(owner);
        setModal(true);

        createUI();
    }

    public void setFilterActive(boolean active) {
        this.filterActive = active;
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Summary Type:"));
        String[] types = {"Daily", "Weekly", "Monthly", "Timeframe"};
        summaryTypeCombo = new JComboBox<>(types);
        summaryTypeCombo.addActionListener(e -> {
            if ("Timeframe".equals(summaryTypeCombo.getSelectedItem())) {
                dateRangeCombo.setVisible(true);
                dateRangeCombo.setEnabled(true);
            } else {
                dateRangeCombo.setVisible(false);
                dateRangeCombo.setEnabled(false);
            }
        });
        topPanel.add(summaryTypeCombo);

        // Date range selector (hidden by default, shown for Timeframe)
        dateRangeCombo = new JComboBox<>();
        String[] dateRanges = {"Last 7 days", "Last 14 days", "Last 30 days", "Current week", "Current month", "All time"};
        for (String range : dateRanges) {
            dateRangeCombo.addItem(range);
        }
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
        } else if ("Weekly".equals(type)) {
            sb.append(generateWeeklySummary());
        } else if ("Monthly".equals(type)) {
            sb.append(generateMonthlySummary());
        } else {
            sb.append(generateTimeframeSummary());
        }

        resultsTextArea.setText(sb.toString());
    }

    /**
     * Daily summary: entries grouped by date.
     * Shows only current file when no filter, all files when date range filter is active.
     */
    private String generateDailySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("DAILY ACTIVITY SUMMARY\n");
        sb.append(filterActive ? "All files (date range filtered)" : currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        List<CSVFile> filesToInclude = filterActive ? allFiles : List.of(currentFile);

        // Group by date, then by description within each date
        Map<LocalDate, Map<String, Double>> dailyByDesc = new HashMap<>();

        for (CSVFile file : filesToInclude) {
            for (ActivityEntry entry : file.getEntries()) {
                LocalDate date = entry.getTimestamp().toLocalDate();
                String desc = entry.getDescription();
                double time = entry.getTimeSpentDays();

                Map<String, Double> dayMap = dailyByDesc.computeIfAbsent(date, k -> new HashMap<>());
                dayMap.put(desc, dayMap.getOrDefault(desc, 0.0) + time);
            }
        }

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(dailyByDesc.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            sb.append("\n").append(date).append(":").append("\n");
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

    /**
     * Weekly summary: entries grouped by week number.
     * Always includes all loaded files.
     */
    private String generateWeeklySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("WEEKLY ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Weekly summary always includes all files
        List<CSVFile> filesToInclude = allFiles;

        // Group by week, then by description within each week
        // Calculate week number manually since YearWeek not available in all JDKs
        Map<String, Map<String, Double>> weeklyByDesc = new HashMap<>();

        for (CSVFile file : filesToInclude) {
            for (ActivityEntry entry : file.getEntries()) {
                LocalDate date = entry.getTimestamp().toLocalDate();
                int weekNum = (date.getDayOfYear() - 1) / 7 + 1;
                String weekKey = date.getYear() + "-W" + String.format("%02d", weekNum);
                String desc = entry.getDescription();
                double time = entry.getTimeSpentDays();

                Map<String, Double> weekMap = weeklyByDesc.computeIfAbsent(weekKey, k -> new HashMap<>());
                weekMap.put(desc, weekMap.getOrDefault(desc, 0.0) + time);
            }
        }

        // Sort weeks
        List<String> sortedWeeks = new ArrayList<>(weeklyByDesc.keySet());
        Collections.sort(sortedWeeks);

        for (String weekKey : sortedWeeks) {
            sb.append("\n").append(weekKey).append(":").append("\n");
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

    /**
     * Monthly summary: entries grouped by month.
     * Always includes all loaded files.
     */
    private String generateMonthlySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("MONTHLY ACTIVITY SUMMARY\n").append(currentFile.getProjectName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Monthly summary always includes all files
        List<CSVFile> filesToInclude = allFiles;

        // Group by month, then by description within each month
        Map<String, Map<String, Double>> monthlyByDesc = new HashMap<>();

        for (CSVFile file : filesToInclude) {
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

        // Sort months
        List<String> sortedMonths = new ArrayList<>(monthlyByDesc.keySet());
        Collections.sort(sortedMonths);

        for (String monthKey : sortedMonths) {
            sb.append("\n").append(monthKey).append(":").append("\n");
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

    /**
     * Timeframe summary: entries for a specific date range.
     * Always includes all loaded files.
     */
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

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(dailyByDesc.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            sb.append("\n").append(date).append(":").append("\n");
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

    // Testing methods
    public String getDailySummary() {
        return generateDailySummary();
    }

    public String getWeeklySummary() {
        return generateWeeklySummary();
    }

    public String getMonthlySummary() {
        return generateMonthlySummary();
    }

    public String getTimeframeSummary() {
        return generateTimeframeSummary();
    }

    public void setDateRangeComboSelectedIndex(int index) {
        dateRangeCombo.setSelectedIndex(index);
    }

    public int getDateRangeComboItemCount() {
        return dateRangeCombo.getItemCount();
    }

    public String getDateRangeComboItemAt(int index) {
        return (String) dateRangeCombo.getItemAt(index);
    }

    public int getSummaryTypeItemCount() {
        return summaryTypeCombo.getItemCount();
    }

    public String getSummaryTypeItemAt(int index) {
        return (String) summaryTypeCombo.getItemAt(index);
    }
}
