package com.recipe.models.enums;

public enum Difficulty {
    EASY("Easy", "🍀", new ColorValue(46, 204, 113)),
    MEDIUM("Medium", "⭐", new ColorValue(241, 196, 15)),
    HARD("Hard", "🔥", new ColorValue(231, 76, 60));
    
    private final String displayName;
    private final String icon;
    private final ColorValue color;
    
    Difficulty(String displayName, String icon, ColorValue color) {
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
    }
    
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public ColorValue getColor() { return color; }
    
    public static Difficulty fromString(String text) {
        if (text == null) return MEDIUM;
        for (Difficulty d : values()) {
            if (d.name().equalsIgnoreCase(text) || d.displayName.equalsIgnoreCase(text)) {
                return d;
            }
        }
        return MEDIUM;
    }
    
    public record ColorValue(int r, int g, int b) {
        public java.awt.Color toAwtColor() {
            return new java.awt.Color(r, g, b);
        }
    }
}