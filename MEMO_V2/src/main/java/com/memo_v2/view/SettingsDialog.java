package com.memo_v2.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class SettingsDialog extends JDialog {
    private JTextField storageDirField;
    private JButton browseButton;

    public SettingsDialog(Frame owner) {
        super(owner, "Settings", true);
        setSize(500, 200);
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

        // Storage Directory
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Storage Directory:"), gbc);
        gbc.gridx = 1;
        storageDirField = new JTextField(30);
        mainPanel.add(storageDirField, gbc);

        // Browse Button
        gbc.gridx = 2;
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int ret = chooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                storageDirField.setText(selectedDir.getAbsolutePath());
            }
        });
        mainPanel.add(browseButton, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String dir = storageDirField.getText();
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                JOptionPane.showMessageDialog(this, "Directory does not exist. Please create it first.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Save settings (in a real app, persist to config file)
            JOptionPane.showMessageDialog(this, "Settings saved!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
        storageDirField.setText("./log");
    }
}
