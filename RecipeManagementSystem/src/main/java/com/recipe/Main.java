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
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;
import com.recipe.auth.SessionManager;
import com.recipe.database.DatabaseConnection;
import com.recipe.gui.MainWindow;
import com.recipe.gui.auth.LoginScreen;

public class Main {
    
    public static void main(String[] args) {
        // Suppress PDF font cache warnings
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("org.apache.fontbox").setLevel(java.util.logging.Level.SEVERE);
        
        // Enable detailed error logging and fix scaling
        System.setProperty("java.awt.headless", "false");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Auto-detect DPI scaling
        String scale = System.getenv("DISPLAY_SCALE");
        if (scale == null) {
            scale = "1.0"; // Default scaling
        }
        System.setProperty("flatlaf.uiScale", scale);
        System.setProperty("sun.java2d.uiScale", scale);
        
        // Set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("=== UNCAUGHT EXCEPTION ===");
            System.err.println("Thread: " + thread.getName());
            System.err.println("Exception: " + throwable.getClass().getSimpleName());
            throwable.printStackTrace(System.err);
            
            try {
                String message = "An unexpected error occurred:\n" + 
                    (throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName());
                JOptionPane.showMessageDialog(null, 
                    message,
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                System.err.println("Failed to show error dialog: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        });
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("Button.focusWidth", 0);
            UIManager.put("Component.focusWidth", 0);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Use default look and feel
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
                        return;
                    }
                    
                    // Start application with error handling
                    try {
                        System.err.println("Starting GUI initialization...");
                        
                        if (SessionManager.getInstance().isLoggedIn()) {
                            System.err.println("User logged in, creating MainWindow...");
                            MainWindow mainWindow = new MainWindow();
                            System.err.println("MainWindow created successfully, showing...");
                            mainWindow.setVisible(true);
                        } else {
                            System.err.println("No user logged in, creating LoginScreen...");
                            LoginScreen loginScreen = new LoginScreen();
                            System.err.println("LoginScreen created successfully, showing...");
                            loginScreen.setVisible(true);
                        }
                    } catch (Exception ex) {
                        System.err.println("=== FAILED DURING GUI INITIALIZATION ===");
                        System.err.println("Exception: " + ex.getClass().getName());
                        System.err.println("Message: " + ex.getMessage());
                        ex.printStackTrace(System.err);
                        
                        JOptionPane.showMessageDialog(null,
                            "Failed to initialize UI:\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                            "GUI Initialization Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                });
                splashTimer.setRepeats(false);
                splashTimer.start();
                
            } catch (Exception e) {
                System.err.println("Failed during application startup:");
                e.printStackTrace(System.err);
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
        content.setBackground(new Color(25, 45, 85));
        
        JLabel logoLabel = new JLabel("🍽️ Recipe Management System", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadingLabel.setForeground(new Color(220, 230, 245));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 6));
        progressBar.setForeground(new Color(41, 128, 185));
        
        content.add(logoLabel, BorderLayout.CENTER);
        content.add(loadingLabel, BorderLayout.SOUTH);
        content.add(progressBar, BorderLayout.NORTH);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        splash.setContentPane(content);
        splash.setSize(550, 320);
        splash.setLocationRelativeTo(null);
        
        return splash;
    }
    
    private static boolean testDatabaseConnection() {
        try {
            DatabaseConnection.getInstance().testConnection();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static void showDatabaseErrorDialog() {
        String message = """
            Cannot connect to database!
            
            Please ensure:
            1. PostgreSQL is installed and running
            2. Database 'recipe_management' exists
            3. Set required environment variables:
               export DB_PASSWORD=your_password
               export DB_USER=postgres (optional)
               export DB_HOST=localhost (optional)
               export DB_PORT=5432 (optional)
               export DB_NAME=recipe_management (optional)
            
            Check the console for details.
            """;
        
        try {
            JOptionPane.showMessageDialog(null, message,
                "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Fallback if message dialog fails
            System.err.println(message);
        }
    }
}
