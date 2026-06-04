package com.recipe.models.enums;

public enum UserRole {
    ADMIN("Administrator", "Full system access"),
    MANAGER("Manager", "Can manage recipes and users"),
    STAFF("Staff", "Basic access - view and create recipes"),
    USER("User", "Standard user access");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    
    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        return USER;
    }
}