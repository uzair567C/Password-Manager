package com.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.recipe.auth.SessionManager;
import com.recipe.database.DatabaseConnection;
import com.recipe.gui.MainWindow;
import com.recipe.gui.auth.LoginScreen;

public class Main {
    
    public static void main(String[] args) {
        // Enable detailed error logging
        System.setProperty("java.awt.headless", "false");
        
        // Set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught Exception in thread " + thread.getName());
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "An error occurred:\n" + throwable.getMessage() + "\n\nCheck console for details.",
                "Application Error", JOptionPane.ERROR_MESSAGE);
        });
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system look and feel: " + ex.getMessage());
            }
        }
        
        // Start application with error handling
        SwingUtilities.invokeLater(() -> {
            try {
                // Show splash screen
                JWindow splash = createSplashScreen();
                splash.setVisible(true);
                
                // Test database connection
                boolean dbConnected = testDatabaseConnection();
                
                // Hide splash after delay
                Timer splashTimer = new Timer(2000, e -> {
                    splash.setVisible(false);
                    splash.dispose();
                    
                    if (!dbConnected) {
                        showDatabaseErrorDialog();
                        System.exit(1);
                    }
                    
                    // Start application
                    if (SessionManager.getInstance().isLoggedIn()) {
                        new MainWindow().setVisible(true);
                    } else {
                        new LoginScreen().setVisible(true);
                    }
                });
                splashTimer.setRepeats(false);
                splashTimer.start();
                
            } catch (Exception e) {
                System.err.println("Fatal error during startup:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to start application:\n" + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    private static JWindow createSplashScreen() {
        JWindow splash = new JWindow();
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(46, 134, 222));
        
        JLabel logoLabel = new JLabel("🍽️ Recipe Management System", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setForeground(new Color(240, 240, 240));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 5));
        
        content.add(logoLabel, BorderLayout.CENTER);
        content.add(loadingLabel, BorderLayout.SOUTH);
        content.add(progressBar, BorderLayout.NORTH);
        
        splash.setContentPane(content);
        splash.setSize(500, 300);
        splash.setLocationRelativeTo(null);
        
        return splash;
    }
    
    private static boolean testDatabaseConnection() {
        try {
            System.out.println("Testing database connection...");
            DatabaseConnection.getInstance().testConnection();
            System.out.println("Database connection successful!");
            return true;
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void showDatabaseErrorDialog() {
        String message = """
            Cannot connect to database!
            
            Please ensure:
            1. PostgreSQL is installed and running
            2. Database 'recipe_management' exists
            3. Connection settings in DatabaseConnection.java are correct
            
            Check the console for details.
            """;
        
        JOptionPane.showMessageDialog(null, message,
            "Database Connection Error", JOptionPane.ERROR_MESSAGE);
    }
}