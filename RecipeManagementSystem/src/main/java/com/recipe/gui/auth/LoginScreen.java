package com.recipe.gui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.recipe.gui.MainWindow;
import com.recipe.models.User;
import com.recipe.services.AuthService;

public class LoginScreen extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JCheckBox showPasswordCheckBox;
    private JProgressBar loadingProgress;
    private JLabel statusLabel;
    private int originalX;
    
    public LoginScreen() {
        this.authService = new AuthService();
        initComponents();
        setupKeyboardShortcuts();
    }
    
    private void initComponents() {
        setTitle("Recipe Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        
        JPanel logoPanel = createLogoPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(logoPanel, gbc);
        
        JPanel formPanel = createFormPanel();
        gbc.gridy = 1;
        mainPanel.add(formPanel, gbc);
        
        loadingProgress = new JProgressBar();
        loadingProgress.setIndeterminate(true);
        loadingProgress.setVisible(false);
        loadingProgress.setPreferredSize(new Dimension(300, 5));
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 20, 5, 20);
        mainPanel.add(loadingProgress, gbc);
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        mainPanel.add(statusLabel, gbc);
        
        JPanel footerPanel = createFooterPanel();
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(footerPanel, gbc);
        
        originalX = getLocation().x;
    }
    
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🍽️ Recipe Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Manage your recipes with ease", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 230, 245));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("📧 Username or Email");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(userLabel, gbc);
        
        gbc.gridy = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(usernameField, gbc);
        
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("🔒 Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(passLabel, gbc);
        
        gbc.gridy = 3;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(passwordField, gbc);
        
        gbc.gridy = 4;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        panel.add(showPasswordCheckBox, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = createStyledButton("Login", new Color(46, 204, 113));
        loginButton.addActionListener(this::performLoginAction);
        panel.add(loginButton, gbc);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        
        registerButton = new JButton("Don't have an account? Register");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setForeground(new Color(220, 230, 245));
        registerButton.setBackground(new Color(41, 128, 185));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegisterScreen());
        
        panel.add(registerButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 45));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 160, 220));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupKeyboardShortcuts() {
        getRootPane().setDefaultButton(loginButton);
        
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> clearFields(), 
            escapeKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> performLoginAction(null),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(Color.WHITE);
    }
    
    private void performLoginAction(ActionEvent e) {
        String usernameOrEmail = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showError("Please enter both username/email and password");
            return;
        }
        
        loginButton.setEnabled(false);
        loadingProgress.setVisible(true);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(new Color(200, 255, 200));
        
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(usernameOrEmail, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    statusLabel.setText("✅ Welcome back, " + user.getUsername() + "!");
                    statusLabel.setForeground(new Color(200, 255, 200));
                    
                    Timer timer = new Timer(800, evt -> {
                        try {
                            MainWindow mainWindow = new MainWindow();
                            mainWindow.setVisible(true);
                            dispose();
                        } catch (Exception ex) {
                            System.err.println("Failed to create MainWindow:");
                            ex.printStackTrace(System.err);
                            showError("Failed to load main window: " + ex.getMessage());
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } catch (Exception ex) {
                    showError(ex.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loadingProgress.setVisible(false);
                    if (!statusLabel.getText().startsWith("✅")) {
                        statusLabel.setText(" ");
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void openRegisterScreen() {
        RegisterScreen registerScreen = new RegisterScreen();
        registerScreen.setVisible(true);
        dispose();
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(new Color(255, 200, 200));
        
        Timer shakeTimer = new Timer(50, null);
        final int[] shakeCount = {0};
        final int startX = this.originalX;
        
        shakeTimer.addActionListener(shakeEvent -> {
            if (shakeCount[0] < 6) {
                setLocation(startX + (shakeCount[0] % 2 == 0 ? -8 : 8), getLocation().y);
                shakeCount[0]++;
            } else {
                setLocation(startX, getLocation().y);
                shakeTimer.stop();
            }
        });
        shakeTimer.start();
        
        Timer clearTimer = new Timer(3000, clearEvent -> {
            if (!statusLabel.getText().startsWith("✅")) {
                statusLabel.setText(" ");
                statusLabel.setForeground(Color.WHITE);
            }
        });
        clearTimer.setRepeats(false);
        clearTimer.start();
    }
    
    private static class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 45, 85), 
                                                  0, getHeight(), new Color(15, 25, 45));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }
}