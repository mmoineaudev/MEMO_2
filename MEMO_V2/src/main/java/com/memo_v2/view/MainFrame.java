     1|package com.memo_v2.view;
     2|
     3|import com.memo_v2.model.ActivityEntry;
     4|import com.memo_v2.model.CSVFile;
     5|import java.time.LocalDate;
     6|import com.memo_v2.view.NewEntryDialog;
     7|import com.memo_v2.view.SearchDialog;
     8|import com.memo_v2.view.SummaryDialog;
     9|import com.memo_v2.view.SettingsDialog;
    10|import com.memo_v2.view.EditDeleteDialog;
    11|import com.memo_v2.view.DateRangeDialog;
    12|import com.memo_v2.view.ExportDialog;
    13|import javax.swing.*;
    14|import javax.swing.border.EmptyBorder;
    15|import javax.swing.table.DefaultTableModel;
    16|import java.awt.*;
    17|import java.io.File;
    18|import java.util.ArrayList;
    19|import java.util.List;
    20|
    21|public class MainFrame extends JFrame {
    22|    private JSplitPane mainSplitPane;
    23|    private JSplitPane centerSplitPane;
    24|    private JList<String> fileListView;
    25|    private DefaultListModel<String> fileListModel;
    26|    private JTable entriesTable;
    27|    private DefaultTableModel entriesTableModel;
    28|    private JTextArea detailTextArea;
    29|    private JLabel statusLabel;
    30|    
    31|    private String storageDirectory = "./log";
    32|    private List<CSVFile> loadedFiles = new ArrayList<>();
    33|    private CSVFile currentFile;
    34|
    35|    // Date range filter
    36|    private LocalDate filterStartDate, filterEndDate;
    37|    
    38|    public MainFrame() {
    39|        setTitle("MEMO_V2 - Activity Tracker");
    40|        setSize(1200, 800);
    41|        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    42|        setLocationByPlatform(true);
    43|        
    44|        createUI();
    45|        setupMenuBar();
    46|        scanFiles();
    47|    }
    48|    
    49|    private void createUI() {
    50|        mainSplitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    51|        
    52|        // Main split pane with three panels
    53|        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    54|        mainSplitPane.setResizeWeight(0.25);
    55|        
    56|        // Left panel - File list
    57|        JPanel leftPanel = createFileListPanel();
    58|        mainSplitPane.setLeftComponent(leftPanel);
    59|        
    60|        // Center split pane for entries and details
    61|        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    62|        centerSplitPane.setResizeWeight(0.75);
    63|        
    64|        // Center panel - Entries table
    65|        JPanel centerPanel = createEntriesPanel();
    66|        centerSplitPane.setLeftComponent(centerPanel);
    67|        
    68|        // Right panel - Details
    69|        JPanel rightPanel = createDetailPanel();
    70|        centerSplitPane.setRightComponent(rightPanel);
    71|        mainSplitPane.setRightComponent(centerSplitPane);
    72|        setContentPane(mainSplitPane);
    73|        
    74|        // Status bar at bottom
    75|        statusLabel = new JLabel("Ready");
    76|        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
    77|        add(statusLabel, BorderLayout.SOUTH);
    78|    }
    79|    
    80|    private JPanel createFileListPanel() {
    81|        JPanel panel = new JPanel(new BorderLayout());
    82|        panel.setBorder(BorderFactory.createTitledBorder("Activity Files"));
    83|        
    84|        fileListModel = new DefaultListModel<>();
    85|        fileListView = new JList<>(fileListModel);
    86|        fileListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    87|        fileListView.addListSelectionListener(e -> {
    88|            if (!e.getValueIsAdjusting()) {
    89|                String selected = fileListView.getSelectedValue();
    90|                if (selected != null) {
    91|                    loadSelectedFile(selected);
    92|                }
    93|            }
    94|        });
    95|        
    96|        JScrollPane scrollPane = new JScrollPane(fileListView);
    97|        panel.add(scrollPane, BorderLayout.CENTER);
    98|        
    99|        return panel;
   100|    }
   101|    
   102|    private JPanel createEntriesPanel() {
   103|        JPanel panel = new JPanel(new BorderLayout());
   104|        panel.setBorder(BorderFactory.createTitledBorder("Activity Entries"));
   105|
   106|        // Filter toolbar
   107|        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
   108|        filterPanel.add(new JLabel("Filter:"));
   109|        JButton dateRangeButton = new JButton("Date Range...");
   110|        dateRangeButton.addActionListener(e -> showDateRangeDialog());
   111|        filterPanel.add(dateRangeButton);
   112|        JButton clearFilterButton = new JButton("Clear Filter");
   113|        clearFilterButton.addActionListener(e -> { filterStartDate = null; filterEndDate = null; loadSelectedFile(currentFile.getFilePath()); });
   114|        filterPanel.add(clearFilterButton);
   115|
   116|        String[] columns = {"Timestamp", "Type", "Description", "Status", "Comment", "Time (days)"};
   117|        entriesTableModel = new DefaultTableModel(columns, 0) {
   118|            @Override
   119|            public boolean isCellEditable(int row, int column) {
   120|                return false;
   121|            }
   122|        };
   123|
   124|        entriesTable = new JTable(entriesTableModel);
   125|        entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   126|        entriesTable.getSelectionModel().addListSelectionListener(e -> {
   127|            if (!e.getValueIsAdjusting()) {
   128|                int selectedRow = entriesTable.getSelectedRow();
   129|                if (selectedRow >= 0) {
   130|                    updateDetailPanel(selectedRow);
   131|                }
   132|            }
   133|        });
   134|
   135|        JScrollPane scrollPane = new JScrollPane(entriesTable);
   136|        panel.add(scrollPane, BorderLayout.CENTER);
   137|        panel.add(filterPanel, BorderLayout.NORTH);
   138|
   139|        return panel;
   140|    }
   141|    
   142|    private JPanel createDetailPanel() {
   143|        JPanel panel = new JPanel(new BorderLayout());
   144|        panel.setBorder(BorderFactory.createTitledBorder("Entry Details"));
   145|        
   146|        detailTextArea = new JTextArea(15, 30);
   147|        detailTextArea.setLineWrap(true);
   148|        detailTextArea.setWrapStyleWord(true);
   149|        detailTextArea.setEditable(false);
   150|        detailTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
   151|        
   152|        JScrollPane scrollPane = new JScrollPane(detailTextArea);
   153|        panel.add(scrollPane, BorderLayout.CENTER);
   154|        
   155|        // Button panel for edit/delete
   156|        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
   157|        JButton editButton = new JButton("Edit");
   158|        editButton.addActionListener(e -> showEditDialog());
   159|        JButton deleteButton = new JButton("Delete");
   160|        deleteButton.addActionListener(e -> showDeleteConfirmation());
   161|        buttonPanel.add(editButton);
   162|        buttonPanel.add(deleteButton);
   163|        
   164|        panel.add(buttonPanel, BorderLayout.SOUTH);
   165|        
   166|        return panel;
   167|    }
   168|    
   169|    private void setupMenuBar() {
   170|        JMenuBar menuBar = new JMenuBar();
   171|        
   172|        // File menu
   173|        JMenu fileMenu = new JMenu("File");
   174|        JMenuItem openItem = new JMenuItem("Open...");
   175|        JMenuItem exitItem = new JMenuItem("Exit");
   176|        exitItem.addActionListener(e -> System.exit(0));
   177|        fileMenu.add(openItem);
   178|        fileMenu.addSeparator();
   179|        fileMenu.add(exitItem);
   180|        menuBar.add(fileMenu);
   181|        
   182|        // Edit menu
   183|        JMenu editMenu = new JMenu("Edit");
   184|        JMenuItem newItem = new JMenuItem("New Entry...");
   185|        newItem.addActionListener(e -> showNewEntryDialog());
   186|        editMenu.add(newItem);
   187|        menuBar.add(editMenu);
   188|        
   189|        // View menu
   190|        JMenu viewMenu = new JMenu("View");
   191|        JMenuItem searchItem = new JMenuItem("Search...");
   192|        searchItem.addActionListener(e -> showSearchDialog());
   193|        viewMenu.add(searchItem);
   194|        JMenuItem dailySummaryItem = new JMenuItem("Daily Summary");
   195|        dailySummaryItem.addActionListener(e -> showDailySummary());
   196|        viewMenu.add(dailySummaryItem);
   197|        JMenuItem weeklySummaryItem = new JMenuItem("Weekly Summary");
   198|        weeklySummaryItem.addActionListener(e -> showWeeklySummary());
   199|        viewMenu.add(weeklySummaryItem);
   200|        menuBar.add(viewMenu);
   201|        
	// Tools menu
	JMenu toolsMenu = new JMenu("Tools");
	JMenuItem settingsItem = new JMenuItem("Settings...");
	settingsItem.addActionListener(e -> showSettingsDialog());
	toolsMenu.add(settingsItem);
	
	JMenuItem exportItem = new JMenuItem("Export...");
	exportItem.addActionListener(e -> showExportDialog());
toolsMenu.add(exportItem);
   207|        menuBar.add(toolsMenu);
   208|        
   209|        setJMenuBar(menuBar);
   210|    }
   211|    
   212|    private void scanFiles() {
   213|        File storageDir = new File(storageDirectory);
   214|        if (!storageDir.exists()) {
   215|            storageDir.mkdirs();
   216|        }
   217|        
   218|        fileListModel.clear();
   219|        loadedFiles.clear();
   220|        
   221|        File[] files = storageDir.listFiles((dir, name) -> name.matches(".*_tracking_\\d{8}\\.csv"));
   222|        if (files != null && files.length > 0) {
   223|            List<File> fileList = new ArrayList<>();
   224|            for (File file : files) {
   225|                fileList.add(file);
   226|            }
   227|            
   228|            // Sort by date descending
   229|            fileList.sort((f1, f2) -> {
   230|                String name1 = f1.getName();
   231|                String name2 = f2.getName();
   232|                int date1 = Integer.parseInt(name1.substring(name1.lastIndexOf('_') + 1, name1.length() - 4));
   233|                int date2 = Integer.parseInt(name2.substring(name2.lastIndexOf('_') + 1, name2.length() - 4));
   234|                return Integer.compare(date2, date1);
   235|            });
   236|            
   237|            for (File file : fileList) {
   238|                String displayName = file.getName();
   239|                fileListModel.addElement(displayName);
   240|                loadedFiles.add(new CSVFile(file.getAbsolutePath()));
   241|            }
   242|            
   243|            if (!loadedFiles.isEmpty()) {
   244|                loadSelectedFile(loadedFiles.get(0).getFilePath());
   245|            }
   246|        }
   247|    }
   248|    
   249|    private void loadSelectedFile(String filePath) {
   250|        try {
   251|            currentFile = new CSVFile(filePath);
   252|            currentFile.loadFromFile();
   253|            entriesTableModel.setRowCount(0);
   254|
   255|            for (ActivityEntry entry : currentFile.getEntries()) {
   256|                // Apply date range filter if set
   257|                if (filterStartDate != null && filterEndDate != null) {
   258|                    java.time.LocalDate entryDate = entry.getTimestamp().toLocalDate();
   259|                    if (!entryDate.isEqual(filterStartDate) && !entryDate.isAfter(filterEndDate)) {
   260|                        continue;
   261|                    }
   262|                }
   263|
   264|                Object[] row = {
   265|                    entry.getTimestampFormatted(),
   266|                    entry.getActivityType(),
   267|                    entry.getDescription(),
   268|                    entry.getStatus(),
   269|                    entry.getComment(),
   270|                    String.format("%.3f", entry.getTimeSpentDays())
   271|                };
   272|                entriesTableModel.addRow(row);
   273|            }
   274|
   275|            int count = currentFile.getEntries().size();
   276|            if (filterStartDate != null && filterEndDate != null) {
   277|                statusLabel.setText(String.format("File: %s | Entries: %d (filtered)", 
   278|                    currentFile.getProjectName(), entriesTableModel.getRowCount()));
   279|            } else {
   280|                statusLabel.setText(String.format("File: %s | Entries: %d", 
   281|                    currentFile.getProjectName(), count));
   282|            }
   283|        } catch (Exception e) {
   284|            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
   285|                "Error", JOptionPane.ERROR_MESSAGE);
   286|        }
   287|    }
   288|    
   289|    private void updateDetailPanel(int row) {
   290|        if (row >= 0 && row < entriesTableModel.getRowCount()) {
   291|            ActivityEntry entry = currentFile.getEntries().get(row);
   292|            StringBuilder sb = new StringBuilder();
   293|            sb.append("Timestamp: ").append(entry.getTimestampFormatted()).append("\n");
   294|            sb.append("Activity Type: ").append(entry.getActivityType()).append("\n");
   295|            sb.append("Description: ").append(entry.getDescription()).append("\n");
   296|            sb.append("Status: ").append(entry.getStatus()).append("\n");
   297|            sb.append("Comment: ").append(entry.getComment()).append("\n");
   298|            sb.append(String.format("Time Spent: %.3f days (%.2f hours)", 
   299|                entry.getTimeSpentDays(), entry.getTimeSpentHours()));
   300|            detailTextArea.setText(sb.toString());
   301|        }
   302|    }
   303|    
   304|    private void showNewEntryDialog() {
   305|        if (currentFile != null) {
   306|            new NewEntryDialog(MainFrame.this, currentFile).setVisible(true);
   307|            // Refresh the table
   308|            scanFiles();
   309|        } else {
   310|            JOptionPane.showMessageDialog(this, "Please select a file first", 
   311|                "No File Selected", JOptionPane.WARNING_MESSAGE);
   312|        }
   313|    }
   314|    
   315|    private void showSearchDialog() {
   316|        new SearchDialog(MainFrame.this).setVisible(true);
   317|    }
   318|    
   319|    private void showDailySummary() {
   320|        new SummaryDialog(MainFrame.this).setVisible(true);
   321|    }
   322|
   323|    private void showWeeklySummary() {
   324|        new SummaryDialog(MainFrame.this).setVisible(true);
   325|    }
   326|    
   327|    private void showSettingsDialog() {
   328|        new SettingsDialog(MainFrame.this).setVisible(true);
   329|    }
   330|
   331|    private void showDateRangeDialog() {
   332|        DateRangeDialog dialog = new DateRangeDialog(MainFrame.this);
   333|        dialog.setVisible(true);
   334|        LocalDate start = dialog.getStartDate();
   335|        LocalDate end = dialog.getEndDate();
   336|        if (start != null && end != null) {
   337|            filterStartDate = start;
   338|            filterEndDate = end;
   339|            loadSelectedFile(currentFile.getFilePath());
   340|        }
   341|    }
   342|    
   343|    private void showEditDialog() {
   344|        int selectedRow = entriesTable.getSelectedRow();
   345|        if (selectedRow >= 0 && currentFile != null) {
   346|            ActivityEntry entry = currentFile.getEntries().get(selectedRow);
   347|            new EditDeleteDialog(MainFrame.this, entry, currentFile).setVisible(true);
   348|            // Refresh the table
   349|            loadSelectedFile(currentFile.getFilePath());
   350|        } else {
   351|            JOptionPane.showMessageDialog(this, "Please select an entry to edit", 
   352|                "No Entry Selected", JOptionPane.WARNING_MESSAGE);
   353|        }
   354|    }
   355|    
   356|    private void showDeleteConfirmation() {
   357|        int selectedRow = entriesTable.getSelectedRow();
   358|        if (selectedRow >= 0 && currentFile != null) {
   359|            ActivityEntry entry = currentFile.getEntries().get(selectedRow);
   360|            String message = String.format("Are you sure you want to delete this entry?\n\n" +
   361|                "Type: %s\nDescription: %s", 
   362|                entry.getActivityType(), entry.getDescription());
   363|            int result = JOptionPane.showConfirmDialog(this, message, 
   364|                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
   365|            if (result == JOptionPane.YES_OPTION) {
   366|                currentFile.getEntries().remove(selectedRow);
   367|                loadSelectedFile(currentFile.getFilePath());
   368|            }
   369|        } else {
   370|            JOptionPane.showMessageDialog(this, "Please select an entry to delete", 
   371|                "No Entry Selected", JOptionPane.WARNING_MESSAGE);
   372|        }
   373|    }

	private void showExportDialog() {
		if (currentFile != null) {
			new ExportDialog(MainFrame.this, currentFile).setVisible(true);
		}
	}
   374|}
   375|