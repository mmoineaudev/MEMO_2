package com.memo_v2.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.time.LocalDate;

public class DateRangeDialog extends JDialog {
    private JSpinner startDateSpinner, endDateSpinner;
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
        
        // Start date
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        startDateSpinner = createDateSpinner(LocalDate.now().getYear() - 1, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        mainPanel.add(startDateSpinner, gbc);
        
        // End date
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        endDateSpinner = createDateSpinner(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        mainPanel.add(endDateSpinner, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply Filter");
        applyButton.addActionListener(e -> {
            selectedStartDate = (LocalDate) startDateSpinner.getValue();
            selectedEndDate = (LocalDate) endDateSpinner.getValue();
            if (selectedStartDate.isAfter(selectedEndDate)) {
                JOptionPane.showMessageDialog(DateRangeDialog.this, 
                    "Start date must be before or equal to end date", 
                    "Invalid Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
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
    
    private JSpinner createDateSpinner(int year, int month, int day) {
        SpinnerDateModel model = new SpinnerDateModel();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        java.util.Date start = cal.getTime();
        cal.set(year, month - 1, day + 1);
        java.util.Date end = cal.getTime();
        model.setStart(start);
        model.setEnd(end);
        
        JSpinner spinner = new JSpinner(model);
        return spinner;
    }
    
    public LocalDate getStartDate() {
        return selectedStartDate;
    }
    
    public LocalDate getEndDate() {
        return selectedEndDate;
    }
}
