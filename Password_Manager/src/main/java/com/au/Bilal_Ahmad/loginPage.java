package com.au.Bilal_Ahmad;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class loginPage {

    // UI Components
    JFrame frame;
    JPanel mainBackground, cardPanel, logInnerPanel, regInnerPanel, topPanel;
    CardLayout cLayout;

    // Theme Button
    JButton themeToggle;

    // Login Components
    protected boolean databaseUser = false; // check login username with data base if match set databaseUser true
    protected boolean databasePass = false; // check login password with data base if match set databasePassword true

    JLabel mainTitleLog, logUserLbl, logPassLbl, noAccountLbl;
    JTextField logUserField;
    JPasswordField logPassField;
    JButton logSubmitBtn, switchToRegBtn, logToggleBtn;
    JLabel logErrorLabel, logUserIcon, logPassIcon;
    JPanel logUserPanel, logPassPanel;

    // Register Components
    protected boolean regUserAvaliable = true;  // Check register Username with data if username aleady taken, Set the regUserAvaliable false otherwise don't set it
    JLabel mainTitleReg, regNameLbl, regUserLbl, regPassLbl, regConfPassLbl, haveAccountLbl;
    JTextField regNameField, regUserField;
    JPasswordField regPassField, regConfPassField;
    JButton regSubmitBtn, switchToLogBtn, regToggleBtn, regConfToggleBtn;
    JLabel regErrorLabel, regNameIcon, regUserIcon, regPassIcon, regConfPassIcon;
    JPanel regNamePanel, regUserPanel, regPassPanel, regConfPassPanel;

    // Theme State
    boolean isDarkMode = true;

    // Standard Theme Colors
    Color darkBgColor = new Color(15, 25, 50);         // Deep background (#0F172A)
    Color darkCardColor = new Color(36, 41, 59);       // Inner panel (#1E293B)
    Color darkInputColor = new Color(15, 23, 42);      // Input background
    Color darkTextColor = Color.WHITE;
    
    Color lightBgColor = new Color(241, 245, 249);     // Light background
    Color lightCardColor = Color.WHITE;                // Inner panel
    Color lightInputColor = new Color(248, 250, 252);  // Input background
    Color lightTextColor = new Color(15, 23, 42);

    Color buttonBlue = new Color(59, 130, 246);        // Action Button (#3B82F6)

    // NEW ICONS (Alternative clean Unicode symbols to match line-art style better)
    String iconUser = "\u263A";       // ☺ (Outline Smiley Face)
    String iconLock = "\uD83D\uDD12"; // 🔒 (Closed Lock - Show)
    String iconEye = "\uD83D\uDC41";  // 👁 (Open Eye - Show)
    String iconHide = "\u2298";       // ⊘ (Slashed Circle - Minimalist Hide/Slashed Eye)

    public loginPage() {
        frame = new JFrame("Password Manager / Login");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(500, 600)); 
        frame.setLayout(new BorderLayout());

        // --- TOP PANEL FOR THEME TOGGLE ---
        topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themeToggle = createFlatButton("☀️ Light Mode");
        themeToggle.setPreferredSize(new Dimension(150, 35));
        topPanel.add(themeToggle);
        frame.add(topPanel, BorderLayout.NORTH);

        // --- MAIN BACKGROUND PANEL ---
        mainBackground = new JPanel(new GridBagLayout());
        frame.add(mainBackground, BorderLayout.CENTER);

        // --- CARD PANEL ---
        cLayout = new CardLayout();
        cardPanel = new JPanel(cLayout);
        cardPanel.setOpaque(false);

        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0; centerGbc.gridy = 0;
        centerGbc.weightx = 1.0; centerGbc.weighty = 1.0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.insets = new Insets(20, 50, 50, 50);

        mainBackground.add(cardPanel, centerGbc);

        // ================= 1. LOGIN FORM =================
        JPanel loginContainer = new JPanel(new BorderLayout());
        loginContainer.setOpaque(false);

        mainTitleLog = new JLabel("PASSWORD MANAGER", SwingConstants.CENTER);
        mainTitleLog.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainTitleLog.setBorder(new EmptyBorder(0, 0, 20, 0));
        loginContainer.add(mainTitleLog, BorderLayout.NORTH);

        logInnerPanel = new JPanel(new GridBagLayout());
        logInnerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        
        logUserLbl = new JLabel("Username");
        gbc.gridy = 0; logInnerPanel.add(logUserLbl, gbc);

        // Username Field with Icon
        logUserIcon = new JLabel(iconUser);
        logUserField = createTextField();
        logUserPanel = createInputWrapper(logUserIcon, logUserField);
        gbc.gridy = 1; logInnerPanel.add(logUserPanel, gbc);

        logPassLbl = new JLabel("Password");
        gbc.gridy = 2; gbc.insets = new Insets(15, 0, 5, 0); logInnerPanel.add(logPassLbl, gbc);

        // Password Field with Icon and Show/Hide Toggle
        logPassIcon = new JLabel(iconLock);
        logPassField = createPasswordField();
        logToggleBtn = new JButton(iconEye);
        logPassPanel = createPasswordWrapper(logPassIcon, logPassField, logToggleBtn);
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 5, 0); logInnerPanel.add(logPassPanel, gbc);

        logErrorLabel = new JLabel("Invalid Credentials!", SwingConstants.CENTER);
        logErrorLabel.setForeground(new Color(239, 68, 68));
        logErrorLabel.setVisible(false);
        gbc.gridy = 4; logInnerPanel.add(logErrorLabel, gbc);

        logSubmitBtn = createPrimaryButton("LOGIN");
        gbc.gridy = 5; gbc.insets = new Insets(20, 0, 10, 0); logInnerPanel.add(logSubmitBtn, gbc);

        // Footer layout (Label + Action Button)
        JPanel switchLogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchLogPanel.setOpaque(false);
        noAccountLbl = new JLabel("Don't have an account?");
        noAccountLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        switchToRegBtn = createFlatButton("> Create an Account <");
        switchToRegBtn.setForeground(buttonBlue); 
        switchLogPanel.add(noAccountLbl);
        switchLogPanel.add(switchToRegBtn);
        
        gbc.gridy = 6; logInnerPanel.add(switchLogPanel, gbc);

        loginContainer.add(logInnerPanel, BorderLayout.CENTER);
        cardPanel.add(loginContainer, "LoginCard");

        // ================= 2. REGISTER FORM =================
        JPanel registerContainer = new JPanel(new BorderLayout());
        registerContainer.setOpaque(false);

        mainTitleReg = new JLabel("PASSWORD MANAGER", SwingConstants.CENTER);
        mainTitleReg.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainTitleReg.setBorder(new EmptyBorder(0, 0, 20, 0));
        registerContainer.add(mainTitleReg, BorderLayout.NORTH);

        regInnerPanel = new JPanel(new GridBagLayout());
        regInnerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints regGbc = new GridBagConstraints();
        regGbc.fill = GridBagConstraints.HORIZONTAL;
        regGbc.weightx = 1.0;
        regGbc.insets = new Insets(2, 0, 2, 0);
        regGbc.gridx = 0;

        regNameLbl = new JLabel("Name");
        regGbc.gridy = 0; regInnerPanel.add(regNameLbl, regGbc);
        regNameIcon = new JLabel(iconUser);
        regNameField = createTextField();
        regNamePanel = createInputWrapper(regNameIcon, regNameField);
        regGbc.gridy = 1; regInnerPanel.add(regNamePanel, regGbc);

        regUserLbl = new JLabel("Username");
        regGbc.gridy = 2; regGbc.insets = new Insets(10, 0, 2, 0); regInnerPanel.add(regUserLbl, regGbc);
        regUserIcon = new JLabel(iconUser);
        regUserField = createTextField();
        regUserPanel = createInputWrapper(regUserIcon, regUserField);
        regGbc.gridy = 3; regGbc.insets = new Insets(2, 0, 2, 0); regInnerPanel.add(regUserPanel, regGbc);

        regPassLbl = new JLabel("Password");
        regGbc.gridy = 4; regGbc.insets = new Insets(10, 0, 2, 0); regInnerPanel.add(regPassLbl, regGbc);
        regPassIcon = new JLabel(iconLock);
        regPassField = createPasswordField();
        regToggleBtn = new JButton(iconEye);
        regPassPanel = createPasswordWrapper(regPassIcon, regPassField, regToggleBtn);
        regGbc.gridy = 5; regGbc.insets = new Insets(2, 0, 2, 0); regInnerPanel.add(regPassPanel, regGbc);

        regConfPassLbl = new JLabel("Confirm Password");
        regGbc.gridy = 6; regGbc.insets = new Insets(10, 0, 2, 0); regInnerPanel.add(regConfPassLbl, regGbc);
        regConfPassIcon = new JLabel(iconLock);
        regConfPassField = createPasswordField();
        regConfToggleBtn = new JButton(iconEye);
        regConfPassPanel = createPasswordWrapper(regConfPassIcon, regConfPassField, regConfToggleBtn);
        regGbc.gridy = 7; regGbc.insets = new Insets(2, 0, 2, 0); regInnerPanel.add(regConfPassPanel, regGbc);

        regErrorLabel = new JLabel("", SwingConstants.CENTER);
        regErrorLabel.setForeground(new Color(239, 68, 68));
        regErrorLabel.setVisible(false);
        regGbc.gridy = 8; regInnerPanel.add(regErrorLabel, regGbc);

        regSubmitBtn = createPrimaryButton("REGISTER");
        regGbc.gridy = 9; regGbc.insets = new Insets(15, 0, 10, 0); regInnerPanel.add(regSubmitBtn, regGbc);

        // Footer layout (Label + Action Button)
        JPanel switchRegPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchRegPanel.setOpaque(false);
        haveAccountLbl = new JLabel("Already have an account?");
        haveAccountLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        switchToLogBtn = createFlatButton("> Log In <");
        switchToLogBtn.setForeground(buttonBlue); 
        switchRegPanel.add(haveAccountLbl);
        switchRegPanel.add(switchToLogBtn);

        regGbc.gridy = 10; regInnerPanel.add(switchRegPanel, regGbc);

        registerContainer.add(regInnerPanel, BorderLayout.CENTER);
        cardPanel.add(registerContainer, "RegisterCard");

        // ---------- EVENT LISTENERS ----------

        // Show/Hide Password Logic
        char defaultEcho = logPassField.getEchoChar();
        setupToggleLogic(logPassField, logToggleBtn, defaultEcho);
        setupToggleLogic(regPassField, regToggleBtn, defaultEcho);
        setupToggleLogic(regConfPassField, regConfToggleBtn, defaultEcho);

        switchToRegBtn.addActionListener(e -> {
            logErrorLabel.setVisible(false);
            cLayout.show(cardPanel, "RegisterCard");
        });

        switchToLogBtn.addActionListener(e -> {
            regErrorLabel.setVisible(false);
            cLayout.show(cardPanel, "LoginCard");
        });

        regSubmitBtn.addActionListener(e -> {
            String name = regNameField.getText();
            String user = regUserField.getText();
            String pass = new String(regPassField.getPassword());
            String confirmPass = new String(regConfPassField.getPassword());

            if(!user.isEmpty() && regUserAvaliable == false){
                regErrorLabel.setText("Username not avaliable!");
                regErrorLabel.setVisible(true);
            } else if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                regErrorLabel.setText("Please fill in all fields!");
                regErrorLabel.setVisible(true);
            } else if(pass.length() < 8){
                regErrorLabel.setText("Password must be at least 8 characters long!");
            } else if (!pass.equals(confirmPass) && !confirmPass.isEmpty()) {
                regErrorLabel.setText("Passwords do not match!");
                regErrorLabel.setVisible(true);
                regConfPassField.setText("");
            }else {
                regErrorLabel.setVisible(false);
                regNameField.setText("");
                regUserField.setText("");
                regPassField.setText("");
                regConfPassField.setText("");
                JOptionPane.showMessageDialog(frame, "Registration Successful!\nWelcome " + name + "!");
                cLayout.show(cardPanel, "LoginCard");
            }
        });

        logSubmitBtn.addActionListener(e -> loginbtn());

        themeToggle.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            themeToggle.setText(isDarkMode ? "☀️ Light Mode" : "🌒 Dark Mode"); 
            applyTheme();
        });

        // Initialize First View Theme
        applyTheme();
        cLayout.show(cardPanel, "LoginCard");

        
    }

    // login getter
    public String getlogUsername(){return logUserField.getText();}
    public String getlogPassword(){
        String pass = new String(logPassField.getPassword());
        return pass;
    }
    
    // registration getter
    public String getregName(){return regNameField.getText();}
    public String getregUsername(){return regUserField.getText();}
    public String getregPassword(){
        String pass = new String(regPassField.getPassword());
        return pass;
    }
    
    private void loginbtn(){
            String user = logUserField.getText();
            String pass = new String(logPassField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                logErrorLabel.setText("Please enter both username and password!");
                logErrorLabel.setVisible(true);
            } else if (databaseUser == false){
                logErrorLabel.setText("Invalid Username!");
                logErrorLabel.setVisible(true);
            } else if(databasePass == false){
                logErrorLabel.setText("Invalid Password!");
                logErrorLabel.setVisible(true);
            }else {
                logErrorLabel.setVisible(false);
                frame.setVisible(false);
                dashboard dash = new dashboard(this);
                dash.frame.setVisible(true);
                dash.frame.revalidate();
                dash.frame.repaint();
            } }


    // Helper: Logic to show and hide passwords
    private void setupToggleLogic(JPasswordField field, JButton toggleBtn, char defaultEcho) {
        toggleBtn.addActionListener(e -> {
            if (field.getEchoChar() == 0) { // Currently showing text -> Switch to hidden
                field.setEchoChar(defaultEcho);
                toggleBtn.setText(iconEye);
            } else { // Currently hidden -> Switch to showing text
                field.setEchoChar((char) 0);
                toggleBtn.setText(iconHide);
            }
        });
    }

    // Helper: Wrap text field to inject an icon on the left cleanly
    private JPanel createInputWrapper(JLabel icon, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 40));
        icon.setBorder(new EmptyBorder(0, 10, 0, 10)); // Inner padding
        field.setBorder(null);      // Remove standard border 
        field.setOpaque(false);     // Allow panel's background to show through
        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // Helper: Wrap password field to inject left icon + right toggle button cleanly
    private JPanel createPasswordWrapper(JLabel icon, JPasswordField field, JButton toggleBtn) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 40));
        icon.setBorder(new EmptyBorder(0, 10, 0, 10)); 
        field.setBorder(null);
        field.setOpaque(false);
        
        toggleBtn.setBorder(new EmptyBorder(0, 10, 0, 10));
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(toggleBtn, BorderLayout.EAST);
        return panel;
    }

    protected JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return field;
    }

    protected JButton createFlatButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(buttonBlue);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Applies the dynamic theme matching the required colors
    protected void applyTheme() {
        Color currentBg = isDarkMode ? darkBgColor : lightBgColor;
        Color currentCardBg = isDarkMode ? darkCardColor : lightCardColor;
        Color currentInputBg = isDarkMode ? darkInputColor : lightInputColor;
        Color currentText = isDarkMode ? darkTextColor : lightTextColor;
        Color fieldBorderColor = isDarkMode ? new Color(71, 85, 105) : new Color(203, 213, 225);

        // Apply Backgrounds
        mainBackground.setBackground(currentBg);
        topPanel.setBackground(currentBg);
        logInnerPanel.setBackground(currentCardBg);
        regInnerPanel.setBackground(currentCardBg);

        // Main Titles & Theme btn
        mainTitleLog.setForeground(currentText);
        mainTitleReg.setForeground(currentText);
        themeToggle.setForeground(currentText);

        // Apply Text Labels
        JLabel[] allLabels = {
            logUserLbl, logPassLbl, regNameLbl, regUserLbl, regPassLbl, regConfPassLbl, noAccountLbl, haveAccountLbl
        };
        for (JLabel lbl : allLabels) {
            lbl.setForeground(currentText);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        }

        // Apply Wrapper Input panels (border & background applied to the container, not text field itself)
        JPanel[] allWrappers = {
            logUserPanel, logPassPanel, regNamePanel, regUserPanel, regPassPanel, regConfPassPanel
        };
        for (JPanel panel : allWrappers) {
            panel.setBackground(currentInputBg);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(fieldBorderColor, 1, true),
                    new EmptyBorder(2, 2, 2, 2)
            ));
        }

        // Apply fields inside the wrappers
        JTextField[] allFields = {
            logUserField, logPassField, regNameField, regUserField, regPassField, regConfPassField
        };
        for (JTextField field : allFields) {
            field.setForeground(currentText);
            field.setCaretColor(currentText);
        }

        // Apply icon/button colors inside the inputs
        JLabel[] allIcons = {logUserIcon, logPassIcon, regNameIcon, regUserIcon, regPassIcon, regConfPassIcon};
        for (JLabel icon : allIcons) { icon.setForeground(currentText); }
        
        JButton[] toggles = {logToggleBtn, regToggleBtn, regConfToggleBtn};
        for (JButton btn : toggles) { btn.setForeground(currentText); }

        // Keep standard link buttons blue regardless of theme
        switchToRegBtn.setForeground(buttonBlue);
        switchToLogBtn.setForeground(buttonBlue);
    }
    
}
