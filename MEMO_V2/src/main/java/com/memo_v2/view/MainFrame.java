package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import java.time.LocalDate;
import com.memo_v2.view.NewEntryDialog;
import com.memo_v2.view.SearchDialog;
import com.memo_v2.view.SummaryDialog;
import com.memo_v2.view.SettingsDialog;
import com.memo_v2.view.EditDeleteDialog;
import com.memo_v2.view.DateRangeDialog;
import com.memo_v2.view.ExportDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private JSplitPane mainSplitPane;
    private JSplitPane centerSplitPane;
    private JList<String> fileListView;
    private DefaultListModel<String> fileListModel;
    private JTable entriesTable;
    private DefaultTableModel entriesTableModel;
    private JTextArea detailTextArea;
    private JLabel statusLabel;
    
    private String storageDirectory = "./log";
    private List<CSVFile> loadedFiles = new ArrayList<>();
    private CSVFile currentFile;

    // Date range filter
    private LocalDate filterStartDate, filterEndDate;
    
    public MainFrame() {
        setTitle("MEMO_V2 - Activity Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        
        createUI();
        setupMenuBar();
        scanFiles();
    }
    
    private void createUI() {
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Main split pane with three panels
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainSplitPane.setResizeWeight(0.25);
        
        // Left panel - File list
        JPanel leftPanel = createFileListPanel();
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Center split pane for entries and details
        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.75);
        
        // Center panel - Entries table
        JPanel centerPanel = createEntriesPanel();
        centerSplitPane.setLeftComponent(centerPanel);
        
        // Right panel - Details
        JPanel rightPanel = createDetailPanel();
        centerSplitPane.setRightComponent(rightPanel);
        mainSplitPane.setRightComponent(centerSplitPane);
        
        // Main container with split pane and status bar
        setLayout(new BorderLayout());
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createFileListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity Files"));
        
        fileListModel = new DefaultListModel<>();
        fileListView = new JList<>(fileListModel);
        fileListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileListView.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = fileListView.getSelectedValue();
                if (selected != null) {
                    loadSelectedFile(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(fileListView);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEntriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity Entries"));

        // Filter toolbar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.add(new JLabel("Filter:"));
        JButton dateRangeButton = new JButton("Date Range...");
        dateRangeButton.addActionListener(e -> showDateRangeDialog());
        filterPanel.add(dateRangeButton);
        JButton clearFilterButton = new JButton("Clear Filter");
        clearFilterButton.addActionListener(e -> { filterStartDate = null; filterEndDate = null; loadSelectedFile(currentFile.getFilePath()); });
        filterPanel.add(clearFilterButton);

        String[] columns = {"Timestamp", "Type", "Description", "Status", "Comment", "Time (days)"};
        entriesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        entriesTable = new JTable(entriesTableModel);
        entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = entriesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    updateDetailPanel(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(entriesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.NORTH);

        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Entry Details"));
        
        detailTextArea = new JTextArea(15, 30);
        detailTextArea.setLineWrap(true);
        detailTextArea.setWrapStyleWord(true);
        detailTextArea.setEditable(false);
        detailTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(detailTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel for edit/delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditDialog());
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> showDeleteConfirmation());
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open...");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem newItem = new JMenuItem("New Entry...");
        newItem.addActionListener(e -> showNewEntryDialog());
        editMenu.add(newItem);
        menuBar.add(editMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem searchItem = new JMenuItem("Search...");
        searchItem.addActionListener(e -> showSearchDialog());
        viewMenu.add(searchItem);
        JMenuItem dailySummaryItem = new JMenuItem("Daily Summary");
        dailySummaryItem.addActionListener(e -> showDailySummary());
        viewMenu.add(dailySummaryItem);
        JMenuItem weeklySummaryItem = new JMenuItem("Weekly Summary");
        weeklySummaryItem.addActionListener(e -> showWeeklySummary());
        viewMenu.add(weeklySummaryItem);
        menuBar.add(viewMenu);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem settingsItem = new JMenuItem("Settings...");
        settingsItem.addActionListener(e -> showSettingsDialog());
        toolsMenu.add(settingsItem);
        menuBar.add(toolsMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void scanFiles() {
        File storageDir = new File(storageDirectory);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        
        fileListModel.clear();
        loadedFiles.clear();
        
        // Check for today's file and create it if absent
        String projectName = "MEMO";
        LocalDate today = java.time.LocalDate.now();
        String todayFileName = String.format("%s_tracking_%04d%02d%02d.csv", 
            projectName, today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        File todayFile = new File(storageDir, todayFileName);
        if (!todayFile.exists()) {
            try {
                CSVFile newFile = new CSVFile(todayFile.getAbsolutePath());
                newFile.createNewFile();
            } catch (Exception e) {
                System.err.println("Failed to create today's file: " + e.getMessage());
            }
        }
        
        File[] files = storageDir.listFiles((dir, name) -> name.matches(".*_tracking_\\d{8}\\.csv"));
        if (files != null && files.length > 0) {
            List<File> fileList = new ArrayList<>();
            for (File file : files) {
                fileList.add(file);
            }
            
            // Sort by date descending, with today's file always first
            fileList.sort((f1, f2) -> {
                boolean isToday1 = f1.getName().equals(todayFileName);
                boolean isToday2 = f2.getName().equals(todayFileName);
                
                if (isToday1 && !isToday2) return -1;
                if (!isToday1 && isToday2) return 1;
                
                // Otherwise sort by date descending
                String name1 = f1.getName();
                String name2 = f2.getName();
                int date1 = Integer.parseInt(name1.substring(name1.lastIndexOf('_') + 1, name1.length() - 4));
                int date2 = Integer.parseInt(name2.substring(name2.lastIndexOf('_') + 1, name2.length() - 4));
                return Integer.compare(date2, date1);
            });
            
            for (File file : fileList) {
                String displayName = file.getName();
                fileListModel.addElement(displayName);
                loadedFiles.add(new CSVFile(file.getAbsolutePath()));
            }
            
            if (!loadedFiles.isEmpty()) {
                // Select the first file (today's file) in the list
                int selectedIndex = 0;
                fileListView.setSelectedIndex(selectedIndex);
                loadSelectedFile(loadedFiles.get(0).getFilePath());
            }
        }
    }
    
    private void loadSelectedFile(String filePath) {
        try {
            currentFile = new CSVFile(filePath);
            currentFile.loadFromFile();
            entriesTableModel.setRowCount(0);

            for (ActivityEntry entry : currentFile.getEntries()) {
                // Apply date range filter if set
                if (filterStartDate != null && filterEndDate != null) {
                    java.time.LocalDate entryDate = entry.getTimestamp().toLocalDate();
                    if (!entryDate.isEqual(filterStartDate) && !entryDate.isAfter(filterEndDate)) {
                        continue;
                    }
                }

                Object[] row = {
                    entry.getTimestampFormatted(),
                    entry.getActivityType(),
                    entry.getDescription(),
                    entry.getStatus(),
                    entry.getComment(),
                    String.format("%.3f", entry.getTimeSpentDays())
                };
                entriesTableModel.addRow(row);
            }

            int count = currentFile.getEntries().size();
            if (filterStartDate != null && filterEndDate != null) {
                statusLabel.setText(String.format("File: %s | Entries: %d (filtered)", 
                    currentFile.getProjectName(), entriesTableModel.getRowCount()));
            } else {
                statusLabel.setText(String.format("File: %s | Entries: %d", 
                    currentFile.getProjectName(), count));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDetailPanel(int row) {
        if (row >= 0 && row < entriesTableModel.getRowCount()) {
            ActivityEntry entry = currentFile.getEntries().get(row);
            StringBuilder sb = new StringBuilder();
            sb.append("Timestamp: ").append(entry.getTimestampFormatted()).append("\n");
            sb.append("Activity Type: ").append(entry.getActivityType()).append("\n");
            sb.append("Description: ").append(entry.getDescription()).append("\n");
            sb.append("Status: ").append(entry.getStatus()).append("\n");
            // Word-wrap comment - replace \n with actual newlines
            String comment = entry.getComment().replace("\\n", "\n");
            sb.append("Comment: ").append(comment).append("\n");
            sb.append(String.format("Time Spent: %.3f days (%.2f hours)", 
                entry.getTimeSpentDays(), entry.getTimeSpentHours()));
            detailTextArea.setText(sb.toString());
        }
    }
    
    private void showNewEntryDialog() {
        if (currentFile != null) {
            new NewEntryDialog(MainFrame.this, currentFile).setVisible(true);
            // Refresh the table by reloading
            loadSelectedFile(currentFile.getFilePath());
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file first", 
                "No File Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showSearchDialog() {
        new SearchDialog(MainFrame.this).setVisible(true);
    }
    
    private void showDailySummary() {
        if (currentFile != null) {
            new SummaryDialog(MainFrame.this, currentFile).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file first", 
                "No File Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showWeeklySummary() {
        // Delegate to same dialog - menu item just sets default selection
        if (currentFile != null) {
            SummaryDialog dialog = new SummaryDialog(MainFrame.this, currentFile);
            dialog.setInitialSummaryType("Weekly");
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file first", 
                "No File Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showSettingsDialog() {
        new SettingsDialog(MainFrame.this).setVisible(true);
    }

    private void showDateRangeDialog() {
        DateRangeDialog dialog = new DateRangeDialog(MainFrame.this);
        dialog.setVisible(true);
        LocalDate start = dialog.getStartDate();
        LocalDate end = dialog.getEndDate();
        if (start != null && end != null) {
            filterStartDate = start;
            filterEndDate = end;
            loadSelectedFile(currentFile.getFilePath());
        }
    }
    
    private void showEditDialog() {
        int selectedRow = entriesTable.getSelectedRow();
        if (selectedRow >= 0 && currentFile != null) {
            ActivityEntry entry = currentFile.getEntries().get(selectedRow);
            new EditDeleteDialog(MainFrame.this, entry, currentFile).setVisible(true);
            // Refresh the table
            loadSelectedFile(currentFile.getFilePath());
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to edit", 
                "No Entry Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showDeleteConfirmation() {
        int selectedRow = entriesTable.getSelectedRow();
        if (selectedRow >= 0 && currentFile != null) {
            ActivityEntry entry = currentFile.getEntries().get(selectedRow);
            String message = String.format("Are you sure you want to delete this entry?\n\n" +
                "Type: %s\nDescription: %s", 
                entry.getActivityType(), entry.getDescription());
            int result = JOptionPane.showConfirmDialog(this, message, 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                currentFile.getEntries().remove(selectedRow);
                loadSelectedFile(currentFile.getFilePath());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete", 
                "No Entry Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}
