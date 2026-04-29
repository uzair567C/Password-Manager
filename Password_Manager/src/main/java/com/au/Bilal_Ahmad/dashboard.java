package com.au.Bilal_Ahmad;


// don't  change anything this , i am working on it , the login page is connected with this page
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;



public class dashboard {

     protected loginPage login;
    
    // UI components
    JFrame frame;
    JPanel mainPanel , toPanel , searchPanel , centerPanel;

    // top panel components
    JLabel welcomeLabel;
    JButton themeToggle;
    JButton logoutButton;

    // search panel components
    JTextField searchField;
    JComboBox<String> priorityBox;
    JButton addCredentialButton;

    // center panel components
    JTable credentialsTable;

    boolean isDarkMode = true; // Default to dark mode
    
    Color darkM_BgColor = new Color(15, 25, 50);       /// Deep main background
    Color otherBgColor = new Color(36, 41, 59);       // Inner panels background
    Color darkInputColor = new Color(15, 23, 42);      // Input background
    Color darkTextColor = Color.WHITE;
    
    Color lightBgColor = new Color(241, 245, 249);     // Light background
    Color lightCardColor = Color.WHITE;                // Inner panel
    Color lightInputColor = new Color(248, 250, 252);  // Input background
    Color lightTextColor = new Color(15, 23, 42);
    
    // Add credential button color
    Color credentialBtnColor = new Color(10 ,153 ,147);

    public dashboard(loginPage login) {
        this.login = login;

        frame = new JFrame("Password Manager / Dashboard");
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(500, 400)); 
        frame.setLayout(new BorderLayout());

        // Top Panel


            toPanel = new JPanel(new BorderLayout());

            welcomeLabel = new JLabel("Welcome, User!", SwingConstants.LEFT);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
            welcomeLabel.setBorder(new EmptyBorder(25, 25, 25, 20)); // Add some padding

            themeToggle = login.createFlatButton("Light Mode ☀");

            logoutButton = createLogoutButton("Logout ⏻"); 
            
            toPanel.add(welcomeLabel, BorderLayout.WEST);

            JPanel topRightPanel = new JPanel();
            topRightPanel.setBorder(new EmptyBorder(25, 0, 25, 25)); // Add some padding
            topRightPanel.setOpaque(false); // Make it transparent to show the background
            topRightPanel.add(themeToggle);
            topRightPanel.add(logoutButton);
            toPanel.add(topRightPanel, BorderLayout.EAST);
            frame.add(toPanel, BorderLayout.NORTH);

        // mainPanel

            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25)); // Add padding around the main panel
            ((BorderLayout) mainPanel.getLayout()).setVgap(25); // Add vertical gap between components
            frame.add(mainPanel, BorderLayout.CENTER);

        // Search Panel

            searchPanel = new JPanel(new GridBagLayout());

            searchField = createSearchField();
            priorityBox = new JComboBox<>(new String[]{"All", "High", "Medium", "Low"});
            addCredentialButton = new JButton("Add Credential");
            addCredentialButton.setBackground(credentialBtnColor);
            addCredentialButton.setForeground(Color.WHITE);
            addCredentialButton.setFocusPainted(false);
            searchPanel.add(searchField);
            searchPanel.add(priorityBox);
            searchPanel.add(addCredentialButton);
            mainPanel.add(searchPanel , BorderLayout.NORTH);


        // Center Panel
            centerPanel = new JPanel(new BorderLayout());
            String[] columnNames = {"Website", "Username", "Password", "Priority"};
            Object[][] data = {
                {"example.com", "user123", "••••••••", "High"},
                {"testsite.com", "admin", "••••••••", "Medium"},
                {"myapp.com", "john_doe", "••••••••", "Low"}
            };
            credentialsTable = new JTable(data, columnNames);
            JScrollPane tableScrollPane = new JScrollPane(credentialsTable);
            centerPanel.add(tableScrollPane, BorderLayout.CENTER);
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            logoutButton.addActionListener(e -> {
                frame.dispose(); // Close the dashboard
                login.frame.setVisible(true); // Show the login page again
                login.logPassField.setText(""); // Clear the password field
            });
            
    
    }


    private JButton createLogoutButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setContentAreaFilled(false);
        return btn;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField("Search");
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals("Search")) {
                    field.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("Search");
                }
            }
        });
        return field;
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new dashboard(new loginPage()));
        loginPage login = new loginPage();
        login.frame.setVisible(true);
}
}
