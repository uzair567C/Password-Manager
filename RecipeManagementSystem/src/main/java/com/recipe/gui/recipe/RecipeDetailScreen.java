package com.recipe.gui.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;
import com.recipe.services.RecipeService;
import com.recipe.utils.ServingScaler;

public class RecipeDetailScreen extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final RecipeService recipeService;
    private final ServingScaler servingScaler;
    private Recipe originalRecipe;
    private Recipe currentRecipe;
    private int currentServings;
    
    private JLabel titleLabel;
    private JLabel difficultyBadge;
    private JLabel ratingLabel;
    private JLabel timeLabel;
    private JLabel servingsLabel;
    private JLabel costLabel;
    private JTextArea descriptionArea;
    private JPanel ingredientsPanel;
    private JPanel nutritionPanel;
    private JButton editButton;
    private JButton deleteButton;
    private JButton cookButton;
    private JButton duplicateButton;
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JButton backButton;
    private JLabel imageLabel;
    private JPanel imagePanel;
    
    private Runnable onCloseListener;
    
    public RecipeDetailScreen(Recipe recipe) {
        this.recipeService = new RecipeService();
        this.servingScaler = new ServingScaler();
        this.originalRecipe = recipe;
        this.currentRecipe = recipe;
        this.currentServings = recipe.servings();
        initComponents();
        loadRecipeData();
    }
    
    public void addOnCloseListener(Runnable listener) {
        this.onCloseListener = listener;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(250, 250, 250));
        
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);
        
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(240, 240, 240));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> closeScreen());
        leftPanel.add(backButton);
        
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        
        editButton = createActionButton("✏️ Edit", new Color(52, 152, 219));
        editButton.addActionListener(e -> openEditScreen());
        rightPanel.add(editButton);
        
        duplicateButton = createActionButton("📋 Duplicate", new Color(155, 89, 182));
        duplicateButton.addActionListener(e -> duplicateRecipe());
        rightPanel.add(duplicateButton);
        
        deleteButton = createActionButton("🗑️ Delete", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteRecipe());
        rightPanel.add(deleteButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(400, 250));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        panel.add(imagePanel, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Difficulty:"), gbc);
        gbc.gridx = 1;
        difficultyBadge = new JLabel();
        infoPanel.add(difficultyBadge, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 1;
        ratingLabel = new JLabel();
        infoPanel.add(ratingLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Total Time:"), gbc);
        gbc.gridx = 1;
        timeLabel = new JLabel();
        infoPanel.add(timeLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Servings:"), gbc);
        gbc.gridx = 1;
        
        JPanel servingsControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        servingsControlPanel.setOpaque(false);
        
        scaleDownButton = new JButton("-");
        scaleDownButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scaleDownButton.setPreferredSize(new Dimension(30, 30));
        scaleDownButton.addActionListener(e -> scaleServings(-1));
        
        servingsLabel = new JLabel(String.valueOf(currentServings));
        servingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        servingsLabel.setPreferredSize(new Dimension(40, 30));
        servingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        scaleUpButton = new JButton("+");
        scaleUpButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scaleUpButton.setPreferredSize(new Dimension(30, 30));
        scaleUpButton.addActionListener(e -> scaleServings(1));
        
        servingsControlPanel.add(scaleDownButton);
        servingsControlPanel.add(servingsLabel);
        servingsControlPanel.add(scaleUpButton);
        
        infoPanel.add(servingsControlPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        costLabel = new JLabel();
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        costLabel.setForeground(new Color(46, 204, 113));
        infoPanel.add(costLabel, gbc);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Description", TitledBorder.LEFT, TitledBorder.TOP));
        
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBackground(Color.WHITE);
        
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(null);
        descScroll.setPreferredSize(new Dimension(400, 100));
        descPanel.add(descScroll, BorderLayout.CENTER);
        
        panel.add(descPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadImage() {
        String photoPath = currentRecipe.photoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                try {
                    BufferedImage img = ImageIO.read(imgFile);
                    if (img != null) {
                        int panelWidth = imagePanel.getWidth();
                        int panelHeight = imagePanel.getHeight();
                        if (panelWidth <= 0) panelWidth = 400;
                        if (panelHeight <= 0) panelHeight = 250;
                        
                        Image scaledImg = img.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaledImg));
                        imageLabel.setText("");
                        return;
                    }
                } catch (IOException e) {
                    System.err.println("Failed to load image: " + e.getMessage());
                }
            }
        }
        imageLabel.setIcon(null);
        imageLabel.setText("<html><div style='text-align: center; font-size: 48px; color: #cccccc;'>" + 
                           currentRecipe.title().substring(0, 1) + "</div></html>");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel ingredientsOuterPanel = new JPanel(new BorderLayout());
        ingredientsOuterPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Ingredients", TitledBorder.LEFT, TitledBorder.TOP));
        
        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.setBackground(Color.WHITE);
        
        JScrollPane ingredientsScroll = new JScrollPane(ingredientsPanel);
        ingredientsScroll.setBorder(null);
        ingredientsScroll.setPreferredSize(new Dimension(350, 200));
        ingredientsOuterPanel.add(ingredientsScroll, BorderLayout.CENTER);
        
        panel.add(ingredientsOuterPanel, BorderLayout.NORTH);
        
        nutritionPanel = new JPanel();
        nutritionPanel.setLayout(new BoxLayout(nutritionPanel, BoxLayout.Y_AXIS));
        nutritionPanel.setBackground(Color.WHITE);
        nutritionPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Nutrition Facts (per serving)", TitledBorder.LEFT, TitledBorder.TOP));
        
        panel.add(nutritionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        cookButton = createActionButton("👨‍🍳 Start Cooking Mode", new Color(46, 204, 113));
        cookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cookButton.addActionListener(e -> startCookingMode());
        panel.add(cookButton);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void loadRecipeData() {
        titleLabel.setText(currentRecipe.title());
        
        String difficulty = currentRecipe.difficulty();
        Color diffColor;
        switch (difficulty.toLowerCase()) {
            case "easy": diffColor = new Color(46, 204, 113); break;
            case "medium": diffColor = new Color(241, 196, 15); break;
            case "hard": diffColor = new Color(231, 76, 60); break;
            default: diffColor = new Color(150, 150, 150); break;
        }
        difficultyBadge.setText(difficulty);
        difficultyBadge.setForeground(diffColor);
        difficultyBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        ratingLabel.setText(createRatingStars(currentRecipe.averageRating()));
        timeLabel.setText(currentRecipe.getFormattedTotalTime());
        
        double costPerServing = currentRecipe.getCostPerServing();
        costLabel.setText(String.format("$%.2f total ($%.2f per serving)", currentRecipe.cost(), costPerServing));
        
        descriptionArea.setText(currentRecipe.description() != null ? currentRecipe.description() : "No description provided.");
        
        loadImage();
        loadIngredients();
        loadNutrition();
    }
    
    private void loadIngredients() {
        ingredientsPanel.removeAll();
        
        List<RecipeIngredient> ingredients = currentRecipe.ingredients();
        if (ingredients == null || ingredients.isEmpty()) {
            JLabel emptyLabel = new JLabel("No ingredients listed");
            emptyLabel.setForeground(new Color(150, 150, 150));
            ingredientsPanel.add(emptyLabel);
        } else {
            for (RecipeIngredient ri : ingredients) {
                JPanel ingredientRow = new JPanel(new BorderLayout());
                ingredientRow.setOpaque(false);
                ingredientRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                String name = ri.ingredient() != null ? ri.ingredient().name() : "Unknown";
                JLabel nameLabel = new JLabel("• " + name);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                JLabel quantityLabel = new JLabel(ri.getFormattedQuantity());
                quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                quantityLabel.setForeground(new Color(100, 100, 100));
                
                ingredientRow.add(nameLabel, BorderLayout.WEST);
                ingredientRow.add(quantityLabel, BorderLayout.EAST);
                
                ingredientsPanel.add(ingredientRow);
            }
        }
        
        ingredientsPanel.revalidate();
        ingredientsPanel.repaint();
    }
    
    private void loadNutrition() {
        nutritionPanel.removeAll();
        
        Nutrition nutrition = currentRecipe.nutrition();
        if (nutrition == null || nutrition.calories() == 0) {
            JLabel emptyLabel = new JLabel("Nutrition information not available");
            emptyLabel.setForeground(new Color(150, 150, 150));
            nutritionPanel.add(emptyLabel);
        } else {
            int caloriesPerServing = nutrition.getCaloriesPerServing(currentServings);
            double proteinPerServing = nutrition.getProteinPerServing(currentServings);
            double carbsPerServing = nutrition.getCarbsPerServing(currentServings);
            double fatPerServing = nutrition.getFatPerServing(currentServings);
            
            addNutritionRow("Calories", caloriesPerServing + " kcal", new Color(231, 76, 60));
            addNutritionRow("Protein", String.format("%.1f g", proteinPerServing), new Color(46, 204, 113));
            addNutritionRow("Carbohydrates", String.format("%.1f g", carbsPerServing), new Color(52, 152, 219));
            addNutritionRow("Fat", String.format("%.1f g", fatPerServing), new Color(241, 196, 15));
        }
        
        nutritionPanel.revalidate();
        nutritionPanel.repaint();
    }
    
    private void addNutritionRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueLabel.setForeground(color);
        
        row.add(labelLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        nutritionPanel.add(row);
    }
    
    private void scaleServings(int delta) {
        int newServings = currentServings + delta;
        if (newServings < 1) return;
        if (newServings > 50) {
            JOptionPane.showMessageDialog(this, "Maximum servings is 50", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentServings = newServings;
        servingsLabel.setText(String.valueOf(currentServings));
        
        currentRecipe = recipeService.scaleRecipe(originalRecipe, currentServings);
        loadIngredients();
        loadNutrition();
        
        double costPerServing = currentRecipe.getCostPerServing();
        costLabel.setText(String.format("$%.2f total ($%.2f per serving)", currentRecipe.cost(), costPerServing));
    }
    
    private void startCookingMode() {
        CookingModeScreen cookingMode = new CookingModeScreen(currentRecipe);
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Cooking Mode - " + currentRecipe.title(), true);
        } else {
            dialog = new JDialog((JDialog) parentWindow, "Cooking Mode - " + currentRecipe.title(), true);
        }
        dialog.setContentPane(cookingMode);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
        
        try {
            recipeService.recordCook(currentRecipe.id());
        } catch (Exception e) {
            System.err.println("Failed to record cook: " + e.getMessage());
        }
    }
    
    private void openEditScreen() {
        AddEditRecipeScreen editScreen = new AddEditRecipeScreen(currentRecipe);
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Edit Recipe", true);
        } else {
            dialog = new JDialog((JDialog) parentWindow, "Edit Recipe", true);
        }
        dialog.setContentPane(editScreen);
        dialog.setSize(1000, 800);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
        
        try {
            currentRecipe = recipeService.getRecipeById(currentRecipe.id()).orElse(currentRecipe);
            originalRecipe = currentRecipe;
            currentServings = currentRecipe.servings();
            loadRecipeData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to refresh: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void duplicateRecipe() {
        try {
            Recipe duplicated = recipeService.duplicateRecipe(currentRecipe.id());
            JOptionPane.showMessageDialog(this, "Recipe duplicated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onCloseListener != null) onCloseListener.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to duplicate: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteRecipe() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + currentRecipe.title() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                recipeService.deleteRecipe(currentRecipe.id());
                JOptionPane.showMessageDialog(this, "Recipe deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                closeScreen();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to delete: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void closeScreen() {
        if (onCloseListener != null) onCloseListener.run();
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof JDialog) {
            ((JDialog) parentWindow).dispose();
        } else if (parentWindow instanceof JFrame) {
            ((JFrame) parentWindow).dispose();
        }
    }
    
    private String createRatingStars(double rating) {
        int fullStars = (int) Math.floor(rating);
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (hasHalfStar) {
            stars.append("½");
        }
        for (int i = stars.length(); i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString() + " " + String.format("%.1f", rating);
    }
}