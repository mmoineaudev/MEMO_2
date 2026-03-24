package com.memo_v2;

import javax.swing.UIManager;
import com.memo_v2.view.MainFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("DEBUG: user.dir = " + System.getProperty("user.dir"));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
