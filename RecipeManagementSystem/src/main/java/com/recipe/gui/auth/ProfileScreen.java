package com.recipe.gui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.recipe.auth.SessionManager;
import com.recipe.models.User;
import com.recipe.services.AuthService;

public class ProfileScreen extends JPanel {  // Changed from JFrame to JPanel
    
    private final AuthService authService;
    private User currentUser;
    
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel fullNameLabel;
    private JLabel memberSinceLabel;
    private JLabel recipesCountLabel;
    private JLabel cookCountLabel;
    private JLabel calorieGoalLabel;
    
    private JButton editProfileButton;
    private JButton changePasswordButton;
    
    public ProfileScreen() {
        this.authService = new AuthService();
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initComponents();
        loadUserData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Info Panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 134, 222));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("👤 My Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(100, 100, 100);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(userIcon, gbc);
        
        gbc.gridx = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(labelColor);
        panel.add(userLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        usernameLabel = new JLabel();
        usernameLabel.setFont(valueFont);
        panel.add(usernameLabel, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel emailIcon = new JLabel("📧");
        emailIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(emailIcon, gbc);
        
        gbc.gridx = 1;
        JLabel emailLabelText = new JLabel("Email:");
        emailLabelText.setFont(labelFont);
        emailLabelText.setForeground(labelColor);
        panel.add(emailLabelText, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        emailLabel = new JLabel();
        emailLabel.setFont(valueFont);
        panel.add(emailLabel, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel nameIcon = new JLabel("👨‍🍳");
        nameIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(nameIcon, gbc);
        
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(labelColor);
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        fullNameLabel = new JLabel();
        fullNameLabel.setFont(valueFont);
        panel.add(fullNameLabel, gbc);
        
        // Member Since
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel calendarIcon = new JLabel("📅");
        calendarIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(calendarIcon, gbc);
        
        gbc.gridx = 1;
        JLabel memberLabel = new JLabel("Member Since:");
        memberLabel.setFont(labelFont);
        memberLabel.setForeground(labelColor);
        panel.add(memberLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        memberSinceLabel = new JLabel();
        memberSinceLabel.setFont(valueFont);
        panel.add(memberSinceLabel, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 20, 10);
        JSeparator separator = new JSeparator();
        panel.add(separator, gbc);
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Recipes Count
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel recipeIcon = new JLabel("📖");
        recipeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(recipeIcon, gbc);
        
        gbc.gridx = 1;
        JLabel recipesLabel = new JLabel("Total Recipes:");
        recipesLabel.setFont(labelFont);
        recipesLabel.setForeground(labelColor);
        panel.add(recipesLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        recipesCountLabel = new JLabel();
        recipesCountLabel.setFont(valueFont);
        panel.add(recipesCountLabel, gbc);
        
        // Cook Count
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel cookIcon = new JLabel("🍳");
        cookIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(cookIcon, gbc);
        
        gbc.gridx = 1;
        JLabel cookLabel = new JLabel("Times Cooked:");
        cookLabel.setFont(labelFont);
        cookLabel.setForeground(labelColor);
        panel.add(cookLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        cookCountLabel = new JLabel();
        cookCountLabel.setFont(valueFont);
        panel.add(cookCountLabel, gbc);
        
        // Calorie Goal
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel calorieIcon = new JLabel("🔥");
        calorieIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(calorieIcon, gbc);
        
        gbc.gridx = 1;
        JLabel calorieLabel = new JLabel("Daily Calorie Goal:");
        calorieLabel.setFont(labelFont);
        calorieLabel.setForeground(labelColor);
        panel.add(calorieLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        calorieGoalLabel = new JLabel();
        calorieGoalLabel.setFont(valueFont);
        panel.add(calorieGoalLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Color.WHITE);
        
        editProfileButton = new JButton("✏️ Edit Profile");
        editProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editProfileButton.setBackground(new Color(52, 152, 219));
        editProfileButton.setForeground(Color.WHITE);
        editProfileButton.setFocusPainted(false);
        editProfileButton.setBorderPainted(false);
        editProfileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editProfileButton.addActionListener(e -> openEditProfileDialog());
        
        changePasswordButton = new JButton("🔒 Change Password");
        changePasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changePasswordButton.setBackground(new Color(155, 89, 182));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setBorderPainted(false);
        changePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePasswordButton.addActionListener(e -> openChangePasswordDialog());
        
        panel.add(editProfileButton);
        panel.add(changePasswordButton);
        
        return panel;
    }
    
    private void loadUserData() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            fullNameLabel.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "Not set");
            memberSinceLabel.setText(currentUser.getCreatedAt() != null ? 
                currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A");
            recipesCountLabel.setText(String.valueOf(currentUser.getTotalRecipes()));
            cookCountLabel.setText(String.valueOf(currentUser.getTotalCookCount()));
            calorieGoalLabel.setText(currentUser.getCalorieGoal() + " calories");
        }
    }
    
    private void openEditProfileDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Profile", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail(), 20);
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(currentUser.getFullName() != null ? currentUser.getFullName() : "", 20);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Daily Calorie Goal:"), gbc);
        
        gbc.gridx = 1;
        JSpinner calorieSpinner = new JSpinner(new SpinnerNumberModel(currentUser.getCalorieGoal(), 500, 5000, 50));
        panel.add(calorieSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                authService.updateProfile(
                    currentUser.getId(),
                    emailField.getText().trim(),
                    nameField.getText().trim(),
                    (Integer) calorieSpinner.getValue()
                );
                currentUser = authService.getCurrentUser();
                loadUserData();
                JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void openChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField currentField = new JPasswordField(20);
        panel.add(currentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField newField = new JPasswordField(20);
        panel.add(newField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField confirmField = new JPasswordField(20);
        panel.add(confirmField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton changeButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");
        
        changeButton.addActionListener(e -> {
            try {
                authService.changePassword(
                    currentUser.getId(),
                    new String(currentField.getPassword()),
                    new String(newField.getPassword()),
                    new String(confirmField.getPassword())
                );
                JOptionPane.showMessageDialog(dialog, "Password changed successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}