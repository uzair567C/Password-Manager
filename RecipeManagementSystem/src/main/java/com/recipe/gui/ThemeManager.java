package com.recipe.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.recipe.auth.SessionManager;
import com.recipe.dao.UserDAO;
import com.recipe.exceptions.DatabaseException;

public class ThemeManager {
    
    private static ThemeManager instance;
    private boolean isDarkMode = false;
    private final Preferences prefs;
    private final UserDAO userDAO;
    private JFrame mainFrame;
    private JPanel sidebar;
    private JPanel topBar;
    private JPanel contentPanel;
    private JButton themeToggleBtn;
    
    // Color definitions
    public static class ThemeColors {
        // Light Theme Colors
        public static final Color LIGHT_BG = new Color(245, 245, 245);
        public static final Color LIGHT_SIDEBAR = new Color(46, 134, 222);
        public static final Color LIGHT_CARD = Color.WHITE;
        public static final Color LIGHT_TEXT = new Color(50, 50, 50);
        public static final Color LIGHT_TEXT_MUTED = new Color(150, 150, 150);
        public static final Color LIGHT_BORDER = new Color(230, 230, 230);
        public static final Color LIGHT_BUTTON = new Color(52, 73, 94);
        public static final Color LIGHT_BUTTON_HOVER = new Color(41, 128, 185);
        
        // Dark Theme Colors
        public static final Color DARK_BG = new Color(30, 30, 36);
        public static final Color DARK_SIDEBAR = new Color(25, 25, 30);
        public static final Color DARK_CARD = new Color(40, 40, 48);
        public static final Color DARK_TEXT = new Color(220, 220, 230);
        public static final Color DARK_TEXT_MUTED = new Color(150, 150, 160);
        public static final Color DARK_BORDER = new Color(55, 55, 65);
        public static final Color DARK_BUTTON = new Color(60, 60, 70);
        public static final Color DARK_BUTTON_HOVER = new Color(80, 80, 90);
        
        // Common Colors
        public static final Color PRIMARY = new Color(46, 134, 222);
        public static final Color PRIMARY_DARK = new Color(41, 128, 185);
        public static final Color SUCCESS = new Color(46, 204, 113);
        public static final Color WARNING = new Color(241, 196, 15);
        public static final Color ERROR = new Color(231, 76, 60);
    }
    
    private ThemeManager() {
        this.prefs = Preferences.userNodeForPackage(ThemeManager.class);
        this.userDAO = new UserDAO();
        loadThemePreference();
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void setMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }
    
    public void setSidebar(JPanel sidebar) {
        this.sidebar = sidebar;
    }
    
    public void setTopBar(JPanel topBar) {
        this.topBar = topBar;
    }
    
    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }
    
    public void setThemeToggleButton(JButton button) {
        this.themeToggleBtn = button;
    }
    
    public void applyTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            customizeUIManager();
            
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
                window.repaint();
            }
            
            if (mainFrame != null) {
                updateMainFrameColors();
            }
            
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
                frame.repaint();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to set theme: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void customizeUIManager() {
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        
        if (isDarkMode) {
            UIManager.put("Panel.background", ThemeColors.DARK_BG);
            UIManager.put("TextField.background", new Color(50, 50, 58));
            UIManager.put("TextArea.background", new Color(50, 50, 58));
            UIManager.put("TextField.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("TextArea.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Label.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Button.background", ThemeColors.DARK_BUTTON);
            UIManager.put("Button.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Table.background", ThemeColors.DARK_CARD);
            UIManager.put("Table.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Table.gridColor", ThemeColors.DARK_BORDER);
            UIManager.put("ScrollPane.background", ThemeColors.DARK_BG);
            UIManager.put("Viewport.background", ThemeColors.DARK_BG);
            UIManager.put("List.background", ThemeColors.DARK_CARD);
            UIManager.put("List.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("ComboBox.background", ThemeColors.DARK_CARD);
            UIManager.put("ComboBox.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Spinner.background", ThemeColors.DARK_CARD);
            UIManager.put("Spinner.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("TitledBorder.titleColor", ThemeColors.DARK_TEXT);
            UIManager.put("TitledBorder.borderColor", ThemeColors.DARK_BORDER);
            UIManager.put("Separator.background", ThemeColors.DARK_BORDER);
            UIManager.put("Separator.foreground", ThemeColors.DARK_BORDER);
            UIManager.put("MenuBar.background", ThemeColors.DARK_SIDEBAR);
            UIManager.put("Menu.background", ThemeColors.DARK_SIDEBAR);
            UIManager.put("Menu.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("MenuItem.background", ThemeColors.DARK_SIDEBAR);
            UIManager.put("MenuItem.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("OptionPane.background", ThemeColors.DARK_CARD);
            UIManager.put("OptionPane.messageForeground", ThemeColors.DARK_TEXT);
            UIManager.put("DesktopPane.background", ThemeColors.DARK_BG);
        } else {
            UIManager.put("Panel.background", ThemeColors.LIGHT_BG);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextField.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("TextArea.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("Label.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("Button.background", ThemeColors.LIGHT_BUTTON);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("Table.gridColor", ThemeColors.LIGHT_BORDER);
            UIManager.put("ScrollPane.background", ThemeColors.LIGHT_BG);
            UIManager.put("Viewport.background", ThemeColors.LIGHT_BG);
            UIManager.put("List.background", Color.WHITE);
            UIManager.put("List.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("Spinner.background", Color.WHITE);
            UIManager.put("Spinner.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("TitledBorder.titleColor", ThemeColors.LIGHT_TEXT);
            UIManager.put("TitledBorder.borderColor", ThemeColors.LIGHT_BORDER);
            UIManager.put("Separator.background", ThemeColors.LIGHT_BORDER);
            UIManager.put("Separator.foreground", ThemeColors.LIGHT_BORDER);
            UIManager.put("MenuBar.background", ThemeColors.LIGHT_CARD);
            UIManager.put("Menu.background", ThemeColors.LIGHT_CARD);
            UIManager.put("Menu.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("MenuItem.background", ThemeColors.LIGHT_CARD);
            UIManager.put("MenuItem.foreground", ThemeColors.LIGHT_TEXT);
            UIManager.put("OptionPane.background", ThemeColors.LIGHT_CARD);
            UIManager.put("OptionPane.messageForeground", ThemeColors.LIGHT_TEXT);
            UIManager.put("DesktopPane.background", ThemeColors.LIGHT_BG);
        }
    }
    
    private void updateMainFrameColors() {
        if (mainFrame == null) return;
        
        if (sidebar != null) {
            sidebar.setBackground(isDarkMode ? ThemeColors.DARK_SIDEBAR : ThemeColors.LIGHT_SIDEBAR);
        }
        
        if (topBar != null) {
            topBar.setBackground(isDarkMode ? ThemeColors.DARK_CARD : ThemeColors.LIGHT_CARD);
            topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getBorderColor()));
        }
        
        if (contentPanel != null) {
            contentPanel.setBackground(getBackgroundColor());
        }
        
        if (themeToggleBtn != null) {
            themeToggleBtn.setText(isDarkMode ? "🌙 Dark Mode" : "☀️ Light Mode");
        }
        
        mainFrame.getContentPane().revalidate();
        mainFrame.getContentPane().repaint();
        mainFrame.repaint();
    }
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveThemePreference();
        
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            customizeUIManager();
            
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
                window.repaint();
            }
            
            updateMainFrameColors();
            
        } catch (Exception e) {
            System.err.println("Failed to toggle theme: " + e.getMessage());
        }
        
        System.out.println("Theme changed to " + getCurrentThemeName() + " Mode");
    }
    
    public void setDarkMode(boolean dark) {
        if (this.isDarkMode != dark) {
            this.isDarkMode = dark;
            saveThemePreference();
            applyTheme();
        }
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    public String getCurrentThemeName() {
        return isDarkMode ? "Dark" : "Light";
    }
    
    private void loadThemePreference() {
        isDarkMode = prefs.getBoolean("darkMode", false);
        
        if (SessionManager.getInstance().isLoggedIn()) {
            try {
                String theme = userDAO.getThemePreference(SessionManager.getInstance().getCurrentUserId());
                if (theme != null) {
                    isDarkMode = "dark".equalsIgnoreCase(theme);
                }
            } catch (DatabaseException e) {
                System.err.println("Failed to load user theme: " + e.getMessage());
            }
        }
    }
    
    private void saveThemePreference() {
        prefs.putBoolean("darkMode", isDarkMode);
        
        if (SessionManager.getInstance().isLoggedIn()) {
            try {
                userDAO.saveThemePreference(SessionManager.getInstance().getCurrentUserId(), 
                    isDarkMode ? "dark" : "light");
            } catch (DatabaseException e) {
                System.err.println("Failed to save user theme: " + e.getMessage());
            }
        }
    }
    
    // Color getter methods
    public Color getPrimaryColor() { return ThemeColors.PRIMARY; }
    public Color getSuccessColor() { return ThemeColors.SUCCESS; }
    public Color getWarningColor() { return ThemeColors.WARNING; }
    public Color getErrorColor() { return ThemeColors.ERROR; }
    
    public Color getTextColor() {
        return isDarkMode ? ThemeColors.DARK_TEXT : ThemeColors.LIGHT_TEXT;
    }
    
    public Color getMutedTextColor() {
        return isDarkMode ? ThemeColors.DARK_TEXT_MUTED : ThemeColors.LIGHT_TEXT_MUTED;
    }
    
    public Color getBackgroundColor() {
        return isDarkMode ? ThemeColors.DARK_BG : ThemeColors.LIGHT_BG;
    }
    
    public Color getCardBackgroundColor() {
        return isDarkMode ? ThemeColors.DARK_CARD : ThemeColors.LIGHT_CARD;
    }
    
    public Color getBorderColor() {
        return isDarkMode ? ThemeColors.DARK_BORDER : ThemeColors.LIGHT_BORDER;
    }
    
    public Color getSidebarColor() {
        return isDarkMode ? ThemeColors.DARK_SIDEBAR : ThemeColors.LIGHT_SIDEBAR;
    }
    
    public Color getButtonColor() {
        return isDarkMode ? ThemeColors.DARK_BUTTON : ThemeColors.LIGHT_BUTTON;
    }
    
    public Color getButtonHoverColor() {
        return isDarkMode ? ThemeColors.DARK_BUTTON_HOVER : ThemeColors.LIGHT_BUTTON_HOVER;
    }
}