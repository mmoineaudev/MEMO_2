package com.memo_v2.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateRangeDialog extends JDialog {
    private JTextField startDateField, endDateField;
    private LocalDate selectedStartDate, selectedEndDate;
    
    public DateRangeDialog(Frame owner) {
        super(owner, "Filter by Date Range", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setModal(true);
        
        createUI();
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Calculate last month's first day for default start date
        LocalDate today = LocalDate.now();
        LocalDate lastMonthStart = today.minusMonths(1).withDayOfMonth(1);
        
        // Start date
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(lastMonthStart.format(formatter), 25);
        mainPanel.add(startDateField, gbc);
        
        // End date
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(today.format(formatter), 25);
        mainPanel.add(endDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply Filter");
        applyButton.addActionListener(e -> {
            try {
                selectedStartDate = LocalDate.parse(startDateField.getText(), formatter);
                selectedEndDate = LocalDate.parse(endDateField.getText(), formatter);
                if (selectedStartDate.isAfter(selectedEndDate)) {
                    JOptionPane.showMessageDialog(DateRangeDialog.this, 
                        "Start date must be before or equal to end date", 
                        "Invalid Range", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use dd/MM/yyyy.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(applyButton);
        
        JButton clearButton = new JButton("Clear Filter");
        clearButton.addActionListener(e -> {
            selectedStartDate = null;
            selectedEndDate = null;
            dispose();
        });
        buttonPanel.add(clearButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        setContentPane(mainPanel);
    }
    
    public LocalDate getStartDate() {
        return selectedStartDate;
    }
    
    public LocalDate getEndDate() {
        return selectedEndDate;
    }
}
