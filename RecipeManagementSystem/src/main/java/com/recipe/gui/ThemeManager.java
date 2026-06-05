package com.recipe.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Centralized theme management system with listener support
 * Manages all color schemes, theme switching, and component updates
 */
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
    
    // List of components to notify when theme changes
    private final List<ThemeListener> themeListeners = new ArrayList<>();
    
    // Color definitions - Professional consistent color scheme
    public static class ThemeColors {
        // ===== LIGHT THEME COLOR PALETTE =====
        public static final Color LIGHT_BG = new Color(248, 250, 252);              // Main background
        public static final Color LIGHT_SIDEBAR = new Color(31, 78, 121);           // Sidebar (dark blue)
        public static final Color LIGHT_CARD = new Color(255, 255, 255);            // Card/Panel background
        public static final Color LIGHT_TEXT = new Color(33, 47, 61);               // Primary text
        public static final Color LIGHT_TEXT_MUTED = new Color(133, 153, 173);      // Secondary text
        public static final Color LIGHT_BORDER = new Color(226, 232, 240);          // Borders
        public static final Color LIGHT_BUTTON = new Color(59, 130, 246);           // Primary button (blue)
        public static final Color LIGHT_BUTTON_HOVER = new Color(37, 99, 235);      // Button hover
        public static final Color LIGHT_BUTTON_ACTIVE = new Color(29, 78, 216);     // Button active
        
        // ===== DARK THEME COLOR PALETTE =====
        public static final Color DARK_BG = new Color(13, 27, 42);                  // Main background (dark blue-black)
        public static final Color DARK_SIDEBAR = new Color(31, 78, 121);            // Sidebar (matching blue)
        public static final Color DARK_CARD = new Color(30, 50, 80);                // Card/Panel background
        public static final Color DARK_TEXT = new Color(235, 241, 245);             // Primary text (light)
        public static final Color DARK_TEXT_MUTED = new Color(156, 163, 175);       // Secondary text
        public static final Color DARK_BORDER = new Color(55, 85, 140);             // Borders
        public static final Color DARK_BUTTON = new Color(59, 130, 246);            // Primary button (blue)
        public static final Color DARK_BUTTON_HOVER = new Color(96, 165, 250);      // Button hover
        public static final Color DARK_BUTTON_ACTIVE = new Color(59, 130, 246);     // Button active
        
        // ===== COMMON STATUS COLORS (Theme-independent) =====
        public static final Color PRIMARY = new Color(59, 130, 246);                // Primary blue
        public static final Color PRIMARY_DARK = new Color(37, 99, 235);            // Darker blue
        public static final Color SUCCESS = new Color(34, 197, 94);                 // Green
        public static final Color WARNING = new Color(250, 204, 21);                // Yellow
        public static final Color ERROR = new Color(239, 68, 68);                   // Red
        public static final Color INFO = new Color(59, 130, 246);                   // Info blue
        
        // ===== WHITE TEXT FOR BUTTONS =====
        public static final Color TEXT_ON_PRIMARY = new Color(255, 255, 255);       // White text on buttons
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
    
    // ===== LISTENER MANAGEMENT =====
    public void addThemeListener(ThemeListener listener) {
        if (listener != null && !themeListeners.contains(listener)) {
            themeListeners.add(listener);
        }
    }
    
    public void removeThemeListener(ThemeListener listener) {
        themeListeners.remove(listener);
    }
    
    private void notifyThemeListeners() {
        for (ThemeListener listener : new ArrayList<>(themeListeners)) {
            try {
                listener.onThemeChanged(isDarkMode);
            } catch (Exception e) {
                // Listener error, continue with others
            }
        }
    }
    
    // ===== COMPONENT REGISTRATION =====
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
    
    // ===== THEME APPLICATION =====
    public void applyTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            customizeUIManager();
            updateAllFrames();
            updateMainFrameColors();
            notifyThemeListeners();
            
        } catch (Exception e) {
            // Theme application failed, use default
        }
    }
    
    private void updateAllFrames() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.repaint();
        }
        
        for (Frame frame : Frame.getFrames()) {
            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();
        }
    }
    
    private void customizeUIManager() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("Button.focusWidth", 0);
        UIManager.put("Component.focusWidth", 0);
        
        if (isDarkMode) {
            UIManager.put("Panel.background", ThemeColors.DARK_BG);
            UIManager.put("TextField.background", new Color(40, 65, 115));
            UIManager.put("TextArea.background", new Color(40, 65, 115));
            UIManager.put("TextField.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("TextArea.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Label.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("Button.background", ThemeColors.DARK_BUTTON);
            UIManager.put("Button.foreground", Color.WHITE);
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
            UIManager.put("CheckBox.foreground", ThemeColors.DARK_TEXT);
            UIManager.put("RadioButton.foreground", ThemeColors.DARK_TEXT);
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
        
        // Update sidebar with consistent colors
        if (sidebar != null) {
            sidebar.setBackground(getSidebarColor());
            sidebar.setForeground(ThemeColors.TEXT_ON_PRIMARY);
            applyThemeRecursive(sidebar);
        }
        
        // Update top bar
        if (topBar != null) {
            topBar.setBackground(getCardBackgroundColor());
            topBar.setForeground(getTextColor());
            topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getBorderColor()));
            applyThemeRecursive(topBar);
        }
        
        // Update content panel
        if (contentPanel != null) {
            contentPanel.setBackground(getBackgroundColor());
            contentPanel.setForeground(getTextColor());
            applyThemeRecursive(contentPanel);
        }
        
        // Update theme toggle button text
        if (themeToggleBtn != null) {
            themeToggleBtn.setText(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
        }
        
        mainFrame.getContentPane().setBackground(getBackgroundColor());
        mainFrame.getContentPane().revalidate();
        mainFrame.getContentPane().repaint();
    }
    
    /**
     * Recursively apply theme colors to all components in a panel
     */
    private void applyThemeRecursive(java.awt.Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            if (!isSpecialPanel(panel)) {
                panel.setBackground(getBackgroundColor());
                panel.setForeground(getTextColor());
            }
        }
        
        if (component instanceof java.awt.Container) {
            java.awt.Container container = (java.awt.Container) component;
            for (java.awt.Component child : container.getComponents()) {
                if (child instanceof JButton) {
                    JButton btn = (JButton) child;
                    if (!isSpecialButton(btn)) {
                        btn.setBackground(getButtonColor());
                        btn.setForeground(ThemeColors.TEXT_ON_PRIMARY);
                    }
                }
                applyThemeRecursive(child);
            }
        }
    }
    
    private boolean isSpecialPanel(JPanel panel) {
        // Don't override panels with special theming
        return panel == sidebar || panel == topBar || panel == contentPanel;
    }
    
    private boolean isSpecialButton(JButton btn) {
        // Check if button has theme toggle or logout text
        String text = btn.getText();
        return text != null && (text.contains("Dark Mode") || text.contains("Light Mode") || 
                                text.contains("Logout") || text.contains("🚪"));
    }
    
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveThemePreference();
        applyTheme();
        notifyThemeListeners();
    }
    
    public void setDarkMode(boolean dark) {
        if (this.isDarkMode != dark) {
            this.isDarkMode = dark;
            saveThemePreference();
            applyTheme();
            notifyThemeListeners();
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
                // Use default theme preference
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
                // Theme preference save failed, continue anyway
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