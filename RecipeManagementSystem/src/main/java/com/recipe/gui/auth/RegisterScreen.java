package com.recipe.gui.auth;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.recipe.auth.PasswordValidator;
import com.recipe.services.AuthService;

public class RegisterScreen extends JFrame {
    
    private static final long serialVersionUID = 1L;

    private final transient AuthService authService;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JButton registerButton;
    private JButton loginButton;
    private JProgressBar passwordStrengthBar;
    private JLabel strengthLabel;
    private JLabel statusLabel;
    
    @SuppressWarnings("this-escape")
    public RegisterScreen() {
        this.authService = new AuthService();
        initComponents();
        setupPasswordStrengthChecker();
    }
    
    private void initComponents() {
        setTitle("Recipe Management System - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 73, 94), 
                                                      0, getHeight(), new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Form fields
        gbc.gridwidth = 1;
        addFormField(mainPanel, gbc, "👤 Username", usernameField = new JTextField(20), 1);
        addFormField(mainPanel, gbc, "📧 Email", emailField = new JTextField(20), 2);
        addFormField(mainPanel, gbc, "👨‍🍳 Full Name (Optional)", fullNameField = new JTextField(20), 3);
        addFormField(mainPanel, gbc, "🔒 Password", passwordField = new JPasswordField(20), 4);
        
        // Password strength indicator
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 20, 5, 5);
        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setPreferredSize(new Dimension(200, 8));
        passwordStrengthBar.setStringPainted(false);
        mainPanel.add(passwordStrengthBar, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 5, 20);
        strengthLabel = new JLabel(" ");
        strengthLabel.setForeground(Color.WHITE);
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        mainPanel.add(strengthLabel, gbc);
        
        addFormField(mainPanel, gbc, "✓ Confirm Password", confirmPasswordField = new JPasswordField(20), 6);
        
        // Register button
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 10, 20);
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(new RegisterAction());
        mainPanel.add(registerButton, gbc);
        
        // Status label
        gbc.gridy = 8;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel, gbc);
        
        // Login link
        gbc.gridy = 9;
        loginButton = new JButton("Already have an account? Login");
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginButton.setForeground(new Color(255, 255, 200));
        loginButton.setBackground(new Color(52, 73, 94));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> openLoginScreen());
        mainPanel.add(loginButton, gbc);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 5, 10);
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 10, 10, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(field, gbc);
    }
    
    private void setupPasswordStrengthChecker() {
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
        });
    }
    
    private void updateStrength() {
        String password = new String(passwordField.getPassword());
        int strength = PasswordValidator.calculateStrength(password);
        passwordStrengthBar.setValue(strength);
        
        Color color;
        if (strength < 30) color = new Color(231, 76, 60);
        else if (strength < 50) color = new Color(230, 126, 34);
        else if (strength < 70) color = new Color(241, 196, 15);
        else if (strength < 85) color = new Color(46, 204, 113);
        else color = new Color(39, 174, 96);
        
        passwordStrengthBar.setForeground(color);
        strengthLabel.setText(PasswordValidator.getStrengthLabel(strength));
        strengthLabel.setForeground(color);
    }
    
    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String fullName = fullNameField.getText().trim();
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all required fields");
                return;
            }
            
            registerButton.setEnabled(false);
            statusLabel.setText("Creating account...");
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    authService.register(username, email, password, confirmPassword, 
                                         fullName.isEmpty() ? null : fullName);
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        showSuccess("Account created successfully! Please login.");
                        
                        Timer timer = new Timer(1500, evt -> {
                            openLoginScreen();
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                    } catch (Exception ex) {
                        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError(message);
                        registerButton.setEnabled(true);
                        statusLabel.setText(" ");
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void openLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
        this.dispose();
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(new Color(255, 200, 200));
        
        // Shake animation
        final int[] shakeCount = {0};
        final int originalX = getLocation().x;
        Timer shakeTimer = new Timer(50, e -> {
            if (shakeCount[0] < 6) {
                setLocation(originalX + (shakeCount[0] % 2 == 0 ? -5 : 5), getLocation().y);
                shakeCount[0]++;
            } else {
                setLocation(originalX, getLocation().y);
                ((Timer) e.getSource()).stop();
            }
        });
        shakeTimer.start();
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(new Color(200, 255, 200));
    }
}