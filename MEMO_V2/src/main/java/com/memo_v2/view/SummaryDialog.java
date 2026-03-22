package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryDialog extends JDialog {
    private JComboBox<String> summaryTypeCombo;
    private JTextArea resultsTextArea;

    public SummaryDialog(Frame owner) {
        super(owner, "Activity Summary", true);
        setSize(700, 600);
        setLocationRelativeTo(owner);
        setModal(true);

        createUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Summary Type:"));
        String[] types = {"Daily", "Weekly"};
        summaryTypeCombo = new JComboBox<>(types);
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
        sb.append("DAILY ACTIVITY SUMMARY\n");
        sb.append("=".repeat(60)).append("\n\n");

        Map<LocalDate, Map<String, Double>> dailySums = new HashMap<>();
        List<File> files = getAllCSVFiles();

        for (File file : files) {
            try {
                CSVFile csvFile = new CSVFile(file.getAbsolutePath());
                csvFile.loadFromFile();

                for (ActivityEntry entry : csvFile.getEntries()) {
                    String desc = entry.getDescription();
                    double time = entry.getTimeSpentDays();
                    // Use timestamp date part as key
                    LocalDate date = entry.getTimestamp().toLocalDate();

                    Map<String, Double> dayMap = dailySums.computeIfAbsent(date, k -> new HashMap<>());
                    dayMap.put(desc, dayMap.getOrDefault(desc, 0.0) + time);
                }
            } catch (Exception e) {
                // Skip unreadable files
            }
        }

        for (LocalDate date : dailySums.keySet()) {
            sb.append("\n").append(date).append(":\n");
            double total = 0.0;
            Map<String, Double> dayMap = dailySums.get(date);
            for (Map.Entry<String, Double> descEntry : dayMap.entrySet()) {
                String desc = descEntry.getKey();
                double timeDays = descEntry.getValue();
                sb.append("  ").append(desc).append(": ");
                sb.append(String.format("%.3f days (%.2f hours)\n", timeDays, timeDays * 24));
                total += timeDays;
            }
            sb.append("  Total: ").append(String.format("%.3f days (%.2f hours)", total, total * 24)).append("\n");
        }

        return sb.toString();
    }

    private String generateWeeklySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("WEEKLY ACTIVITY SUMMARY\n");
        sb.append("=".repeat(60)).append("\n\n");

        Map<String, Map<String, Double>> weeklySums = new HashMap<>();
        List<File> files = getAllCSVFiles();

        for (File file : files) {
            try {
                CSVFile csvFile = new CSVFile(file.getAbsolutePath());
                csvFile.loadFromFile();

                for (ActivityEntry entry : csvFile.getEntries()) {
                    String desc = entry.getDescription();
                    double time = entry.getTimeSpentDays();
                    LocalDate date = entry.getTimestamp().toLocalDate();
                    // Week key: "YYYY-Www"
                    int week = date.getDayOfYear();
                    int weekNum = (week - 1) / 7 + 1;
                    String weekKey = date.getYear() + "-W" + String.format("%02d", weekNum);

                    Map<String, Double> weekMap = weeklySums.computeIfAbsent(weekKey, k -> new HashMap<>());
                    weekMap.put(desc, weekMap.getOrDefault(desc, 0.0) + time);
                }
            } catch (Exception e) {
                // Skip unreadable files
            }
        }

        for (String weekKey : weeklySums.keySet()) {
            sb.append("\n").append(weekKey).append(":\n");
            double total = 0.0;
            Map<String, Double> weekMap = weeklySums.get(weekKey);
            for (Map.Entry<String, Double> descEntry : weekMap.entrySet()) {
                String desc = descEntry.getKey();
                double timeDays = descEntry.getValue();
                sb.append("  ").append(desc).append(": ");
                sb.append(String.format("%.3f days (%.2f hours)\n", timeDays, timeDays * 24));
                total += timeDays;
            }
            sb.append("  Total: ").append(String.format("%.3f days (%.2f hours)", total, total * 24)).append("\n");
        }

        return sb.toString();
    }

    private List<File> getAllCSVFiles() {
        java.util.List<File> files = new ArrayList<>();
        File storageDir = new File("./log");
        if (storageDir.exists()) {
            for (File f : storageDir.listFiles((d, n) -> n.matches(".*_tracking_\\d{8}\\.csv"))) {
                files.add(f);
            }
        }
        return files;
    }
}
