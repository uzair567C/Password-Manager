package com.recipe.gui;

/**
 * Interface for components that need to respond to theme changes
 */
public interface ThemeListener {
    /**
     * Called when the theme changes (dark/light mode toggle)
     */
    void onThemeChanged(boolean isDarkMode);
}
