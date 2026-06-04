package com.recipe.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.recipe.auth.SessionManager;
import com.recipe.gui.auth.LoginScreen;
import com.recipe.gui.auth.ProfileScreen;
import com.recipe.gui.mealplan.NutritionPanel;
import com.recipe.gui.mealplan.WeekPlannerScreen;
import com.recipe.gui.recipe.RecipeListScreen;
import com.recipe.gui.search.DashboardScreen;
import com.recipe.gui.search.SearchScreen;
import com.recipe.models.User;

public class MainWindow extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private JPanel contentPanel;
    private JButton dashboardBtn;
    private JButton recipesBtn;
    private JButton plannerBtn;
    private JButton searchBtn;
    private JButton nutritionBtn;
    private JButton profileBtn;
    private JButton logoutBtn;
    private JButton themeToggleBtn;
    private JLabel userLabel;
    private JPanel topBar;
    private JPanel sidebar;
    
    public MainWindow() {
        ThemeManager.getInstance().setMainFrame(this);
        initComponents();
        setupKeyboardShortcuts();
        showDashboard();
        
        // Register components with ThemeManager
        ThemeManager.getInstance().setSidebar(sidebar);
        ThemeManager.getInstance().setTopBar(topBar);
        ThemeManager.getInstance().setContentPanel(contentPanel);
        ThemeManager.getInstance().setThemeToggleButton(themeToggleBtn);
        
        // Apply initial theme
        ThemeManager.getInstance().applyTheme();
    }
    
    private void initComponents() {
        setTitle("Recipe Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 600));
        
        setLayout(new BorderLayout());
        
        sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);
        
        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);
        
        // Register screens
        DashboardScreen dashboardScreen = new DashboardScreen();
        RecipeListScreen recipeListScreen = new RecipeListScreen();
        WeekPlannerScreen weekPlannerScreen = new WeekPlannerScreen();
        SearchScreen searchScreen = new SearchScreen();
        NutritionPanel nutritionPanel = new NutritionPanel();
        ProfileScreen profileScreen = new ProfileScreen();
        
        contentPanel.add(dashboardScreen, "dashboard");
        contentPanel.add(recipeListScreen, "recipes");
        contentPanel.add(weekPlannerScreen, "planner");
        contentPanel.add(searchScreen, "search");
        contentPanel.add(nutritionPanel, "nutrition");
        contentPanel.add(profileScreen, "profile");
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoLabel = new JLabel("🍽️ Recipe Manager");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        dashboardBtn = createSidebarButton("📊 Dashboard", "dashboard");
        recipesBtn = createSidebarButton("📖 My Recipes", "recipes");
        plannerBtn = createSidebarButton("📅 Meal Planner", "planner");
        searchBtn = createSidebarButton("🔍 Search & Discover", "search");
        nutritionBtn = createSidebarButton("🥗 Nutrition Tracking", "nutrition");
        profileBtn = createSidebarButton("👤 My Profile", "profile");
        
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(recipesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(plannerBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(searchBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(nutritionBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(profileBtn);
        
        sidebar.add(Box.createVerticalGlue());
        
        themeToggleBtn = new JButton("☀️ Light Mode");
        themeToggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeToggleBtn.setMaximumSize(new Dimension(200, 35));
        themeToggleBtn.setBackground(new Color(60, 60, 70));
        themeToggleBtn.setForeground(Color.WHITE);
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.addActionListener(e -> ThemeManager.getInstance().toggleTheme());
        sidebar.add(themeToggleBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(200, 35));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        return sidebar;
    }
    
    private JButton createSidebarButton(String text, String screenName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(210, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.addActionListener(e -> switchScreen(screenName));
        return button;
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        userLabel = new JLabel("Welcome, " + (currentUser != null ? currentUser.getUsername() : "Guest") + "!");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topBar.add(userLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(150, 150, 150));
        topBar.add(dateLabel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private void switchScreen(String screenName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, screenName);
    }
    
    private void showDashboard() {
        switchScreen("dashboard");
    }
    
    private void setupKeyboardShortcuts() {
        getRootPane().registerKeyboardAction(e -> switchScreen("dashboard"),
            KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> switchScreen("recipes"),
            KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> switchScreen("planner"),
            KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> switchScreen("search"),
            KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> switchScreen("nutrition"),
            KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> switchScreen("profile"),
            KeyStroke.getKeyStroke(KeyEvent.VK_6, KeyEvent.ALT_DOWN_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            new LoginScreen().setVisible(true);
            dispose();
        }
    }
}