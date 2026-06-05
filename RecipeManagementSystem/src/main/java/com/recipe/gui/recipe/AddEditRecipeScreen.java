package com.recipe.gui.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.recipe.auth.SessionManager;
import com.recipe.dao.IngredientDAO;
import com.recipe.gui.ThemeManager;
import com.recipe.models.Ingredient;
import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;
import com.recipe.services.RecipeService;

public class AddEditRecipeScreen extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final RecipeService recipeService;
    private final IngredientDAO ingredientDAO;
    private final Recipe existingRecipe;
    private final boolean isEditMode;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner prepTimeSpinner;
    private JSpinner cookTimeSpinner;
    private JSpinner servingsSpinner;
    private JComboBox<String> difficultyCombo;
    private JComboBox<String> categoryCombo;
    
    private DefaultListModel<RecipeIngredient> ingredientsListModel;
    private JList<RecipeIngredient> ingredientsList;
    private JButton addIngredientButton;
    private JButton removeIngredientButton;
    
    private JSpinner caloriesSpinner;
    private JSpinner proteinSpinner;
    private JSpinner carbsSpinner;
    private JSpinner fatSpinner;
    
    private JButton saveButton;
    private JButton cancelButton;
    private JButton uploadImageButton;
    private JLabel imagePreviewLabel;
    private File selectedImageFile;
    private String imagePath;
    
    public AddEditRecipeScreen(Recipe recipe) {
        this.recipeService = new RecipeService();
        this.ingredientDAO = new IngredientDAO();
        this.existingRecipe = recipe;
        this.isEditMode = recipe != null;
        this.imagePath = recipe != null ? recipe.photoPath() : null;
        initComponents();
        if (isEditMode) {
            loadRecipeData();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Image Upload Section
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel imageLabel = new JLabel("Recipe Image:");
        imageLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(imageLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        
        JPanel imageUploadPanel = new JPanel(new BorderLayout(10, 10));
        imageUploadPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        imageUploadPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getInstance().getBorderColor()),
            new EmptyBorder(10, 10, 10, 10)
        ));
        imageUploadPanel.setPreferredSize(new Dimension(400, 200));
        
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setText("No Image Selected\n\nClick 'Upload Image' to add a picture");
        imagePreviewLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        imagePreviewLabel.setForeground(ThemeManager.getInstance().getTextColor());
        imagePreviewLabel.setPreferredSize(new Dimension(380, 150));
        
        uploadImageButton = new JButton("📷 Upload Image");
        uploadImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uploadImageButton.setBackground(new Color(52, 152, 219));
        uploadImageButton.setForeground(Color.WHITE);
        uploadImageButton.setFocusPainted(false);
        uploadImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadImageButton.addActionListener(e -> uploadImage());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        buttonPanel.add(uploadImageButton);
        
        imageUploadPanel.add(imagePreviewLabel, BorderLayout.CENTER);
        imageUploadPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        formPanel.add(imageUploadPanel, gbc);
        
        row++;
        // Title
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel titleLabel = new JLabel("Recipe Title:*");
        titleLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(titleLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        titleField = new JTextField(30);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        titleField.setForeground(ThemeManager.getInstance().getTextColor());
        titleField.setCaretColor(ThemeManager.getInstance().getTextColor());
        formPanel.add(titleField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        descriptionArea.setForeground(ThemeManager.getInstance().getTextColor());
        descriptionArea.setCaretColor(ThemeManager.getInstance().getTextColor());
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        formPanel.add(descScroll, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel prepLabel = new JLabel("Prep Time (min):");
        prepLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(prepLabel, gbc);
        gbc.gridx = 1;
        prepTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 480, 5));
        formPanel.add(prepTimeSpinner, gbc);
        
        gbc.gridx = 2;
        JLabel cookLabel = new JLabel("Cook Time (min):");
        cookLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(cookLabel, gbc);
        gbc.gridx = 3;
        cookTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 480, 5));
        formPanel.add(cookTimeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel servingsLabel = new JLabel("Servings:*");
        servingsLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(servingsLabel, gbc);
        gbc.gridx = 1;
        servingsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));
        formPanel.add(servingsSpinner, gbc);
        
        gbc.gridx = 2;
        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(difficultyLabel, gbc);
        gbc.gridx = 3;
        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyCombo.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        difficultyCombo.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(difficultyCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(categoryLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        categoryCombo = new JComboBox<>(new String[]{"General", "Italian", "Chinese", "Mexican", "Indian", "Breakfast", "Dinner", "Dessert"});
        categoryCombo.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        categoryCombo.setForeground(ThemeManager.getInstance().getTextColor());
        formPanel.add(categoryCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        JPanel ingredientsSection = createIngredientsSection();
        formPanel.add(ingredientsSection, gbc);
        
        row++;
        JPanel nutritionSection = createNutritionSection();
        formPanel.add(nutritionSection, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeManager.getInstance().getBackgroundColor());
        scrollPane.getViewport().setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomButtonPanel = createButtonPanel();
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }
    
    private String copyImageToProject(File sourceFile) {
        try {
            String imagesDir = "images/recipes/";
            File dir = new File(imagesDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalName = sourceFile.getName();
            String extension = "";
            int lastDot = originalName.lastIndexOf(".");
            if (lastDot > 0) {
                extension = originalName.substring(lastDot);
            } else {
                extension = ".jpg";
            }
            
            String fileName = timestamp + extension;
            String destPath = imagesDir + fileName;
            
            File destFile = new File(destPath);
            java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            return destPath;
            
        } catch (IOException e) {
            return sourceFile.getAbsolutePath();
        }
    }
    
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
        fileChooser.setDialogTitle("Select Recipe Image");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            imagePath = copyImageToProject(selectedImageFile);
            
            try {
                BufferedImage img = ImageIO.read(selectedImageFile);
                if (img != null) {
                    int maxWidth = 350;
                    int maxHeight = 140;
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();
                    
                    double ratio = Math.min((double) maxWidth / imgWidth, (double) maxHeight / imgHeight);
                    int scaledWidth = (int) (imgWidth * ratio);
                    int scaledHeight = (int) (imgHeight * ratio);
                    
                    Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImg));
                    imagePreviewLabel.setText("");
                    imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    imagePreviewLabel.setText("Failed to load image");
                    imagePreviewLabel.setIcon(null);
                }
            } catch (IOException e) {
                imagePreviewLabel.setText("Failed to load image: " + e.getMessage());
                imagePreviewLabel.setIcon(null);
            }
        }
    }
    
    private JPanel createIngredientsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getInstance().getBorderColor()),
            "Ingredients",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            ThemeManager.getInstance().getTextColor()
        ));
        
        ingredientsListModel = new DefaultListModel<>();
        ingredientsList = new JList<>(ingredientsListModel);
        ingredientsList.setCellRenderer(new IngredientListRenderer());
        ingredientsList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ingredientsList.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        ingredientsList.setForeground(ThemeManager.getInstance().getTextColor());
        JScrollPane listScroll = new JScrollPane(ingredientsList);
        listScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getInstance().getBorderColor()));
        listScroll.setPreferredSize(new Dimension(400, 150));
        panel.add(listScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        addIngredientButton = new JButton("+ Add Ingredient");
        addIngredientButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addIngredientButton.setBackground(new Color(46, 204, 113));
        addIngredientButton.setForeground(Color.WHITE);
        addIngredientButton.setFocusPainted(false);
        addIngredientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addIngredientButton.addActionListener(e -> addIngredient());
        
        removeIngredientButton = new JButton("- Remove");
        removeIngredientButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeIngredientButton.setBackground(new Color(231, 76, 60));
        removeIngredientButton.setForeground(Color.WHITE);
        removeIngredientButton.setFocusPainted(false);
        removeIngredientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeIngredientButton.addActionListener(e -> removeIngredient());
        
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(removeIngredientButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createNutritionSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getInstance().getBorderColor()),
            "Nutrition Facts (per recipe)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            ThemeManager.getInstance().getTextColor()
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel caloriesLabel = new JLabel("Calories:");
        caloriesLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(caloriesLabel, gbc);
        gbc.gridx = 1;
        caloriesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 50));
        panel.add(caloriesSpinner, gbc);
        
        gbc.gridx = 2;
        JLabel proteinLabel = new JLabel("Protein (g):");
        proteinLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(proteinLabel, gbc);
        gbc.gridx = 3;
        proteinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 5));
        panel.add(proteinSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel carbsLabel = new JLabel("Carbs (g):");
        carbsLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(carbsLabel, gbc);
        gbc.gridx = 1;
        carbsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 5));
        panel.add(carbsSpinner, gbc);
        
        gbc.gridx = 2;
        JLabel fatLabel = new JLabel("Fat (g):");
        fatLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(fatLabel, gbc);
        gbc.gridx = 3;
        fatSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 5));
        panel.add(fatSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getInstance().getBorderColor()));
        
        saveButton = new JButton(isEditMode ? "Update My Recipe" : "Create My Recipe");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveRecipe());
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void addIngredient() {
        JDialog dialog = new JDialog((JDialog) SwingUtilities.getWindowAncestor(this), "Add Ingredient", true);
        dialog.setSize(550, 520);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Section 1: Select Existing Ingredient
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel existingLabel = new JLabel("Select Existing Ingredient:");
        existingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        existingLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(existingLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Load existing ingredients from database - declare list outside try block
        List<Ingredient> existingIngredients = new ArrayList<>();
        try {
            existingIngredients = ingredientDAO.getAllIngredients();
        } catch (Exception e) {
            System.err.println("Failed to load ingredients: " + e.getMessage());
            JLabel errorLabel = new JLabel("Error loading ingredients: " + e.getMessage());
            errorLabel.setForeground(ThemeManager.getInstance().getErrorColor());
            panel.add(errorLabel, gbc);
        }
        
        if (!existingIngredients.isEmpty()) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Ingredient ing : existingIngredients) {
                listModel.addElement(ing.name() + " (" + ing.unit() + ")");
            }
            
            JList<String> ingredientList = new JList<>(listModel);
            ingredientList.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
            ingredientList.setForeground(ThemeManager.getInstance().getTextColor());
            ingredientList.setSelectionBackground(new Color(52, 152, 219));
            ingredientList.setSelectionForeground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(ingredientList);
            scrollPane.setPreferredSize(new Dimension(450, 120));
            panel.add(scrollPane, gbc);
            
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
            
            JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
            
            selectPanel.add(new JLabel("Quantity:"));
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 1000, 0.5));
            quantitySpinner.setPreferredSize(new Dimension(80, 30));
            selectPanel.add(quantitySpinner);
            
            JButton selectButton = new JButton("Add Selected Ingredient");
            selectButton.setBackground(new Color(46, 204, 113));
            selectButton.setForeground(Color.WHITE);
            selectButton.setFocusPainted(false);
            
            // Use final copy for the lambda
            final List<Ingredient> finalExistingIngredients = existingIngredients;
            selectButton.addActionListener(e -> {
                int selected = ingredientList.getSelectedIndex();
                if (selected >= 0) {
                    Ingredient selectedIng = finalExistingIngredients.get(selected);
                    BigDecimal qty = BigDecimal.valueOf((Double) quantitySpinner.getValue());
                    
                    RecipeIngredient ri = RecipeIngredient.builder()
                        .recipeId(0)
                        .ingredient(selectedIng)
                        .quantity(qty)
                        .unit(selectedIng.unit())
                        .build();
                    
                    ingredientsListModel.addElement(ri);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(AddEditRecipeScreen.this, "Ingredient added: " + selectedIng.name(), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select an ingredient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            selectPanel.add(selectButton);
            panel.add(selectPanel, gbc);
        } else {
            JLabel noIngredientsLabel = new JLabel("No ingredients found in database. Please add a new ingredient below.");
            noIngredientsLabel.setForeground(ThemeManager.getInstance().getErrorColor());
            panel.add(noIngredientsLabel, gbc);
        }
        
        // Separator
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 8, 15, 8);
        JSeparator separator = new JSeparator();
        separator.setForeground(ThemeManager.getInstance().getBorderColor());
        panel.add(separator, gbc);
        
        // Section 2: Add New Ingredient
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 8, 8, 8);
        JLabel newLabel = new JLabel("Or Add New Ingredient:");
        newLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newLabel.setForeground(ThemeManager.getInstance().getTextColor());
        panel.add(newLabel, gbc);
        
        gbc.gridy = 5;
        JPanel newIngredientPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        newIngredientPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        
        JTextField nameField = new JTextField();
        nameField.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        nameField.setForeground(ThemeManager.getInstance().getTextColor());
        nameField.setCaretColor(ThemeManager.getInstance().getTextColor());
        
        JSpinner newQuantitySpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 1000, 0.5));
        
        JComboBox<String> unitCombo = new JComboBox<>(new String[]{"g", "kg", "ml", "L", "cup", "tbsp", "tsp", "piece"});
        unitCombo.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        unitCombo.setForeground(ThemeManager.getInstance().getTextColor());
        
        newIngredientPanel.add(new JLabel("Ingredient Name:*"));
        newIngredientPanel.add(nameField);
        newIngredientPanel.add(new JLabel("Quantity:*"));
        newIngredientPanel.add(newQuantitySpinner);
        newIngredientPanel.add(new JLabel("Unit:*"));
        newIngredientPanel.add(unitCombo);
        
        panel.add(newIngredientPanel, gbc);
        
        gbc.gridy = 6;
        JButton addNewButton = new JButton("Create and Add New Ingredient");
        addNewButton.setBackground(new Color(52, 152, 219));
        addNewButton.setForeground(Color.WHITE);
        addNewButton.setFocusPainted(false);
        addNewButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter ingredient name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String unit = (String) unitCombo.getSelectedItem();
            BigDecimal quantity = BigDecimal.valueOf((Double) newQuantitySpinner.getValue());
            
            try {
                // First check if ingredient already exists
                Ingredient existingIng = null;
                try {
                    existingIng = ingredientDAO.findByName(name).orElse(null);
                } catch (Exception ex) {
                    // Ingredient not found - that's fine
                }
                
                Ingredient savedIngredient;
                if (existingIng != null) {
                    savedIngredient = existingIng;
                    JOptionPane.showMessageDialog(dialog, "Ingredient '" + name + "' already exists. Using existing ingredient.", 
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Save new ingredient to database
                    Ingredient newIngredient = Ingredient.builder()
                        .name(name)
                        .unit(unit)
                        .unitPrice(BigDecimal.ZERO)
                        .caloriesPerUnit(BigDecimal.ZERO)
                        .proteinPerUnit(BigDecimal.ZERO)
                        .carbsPerUnit(BigDecimal.ZERO)
                        .fatPerUnit(BigDecimal.ZERO)
                        .build();
                    
                    savedIngredient = ingredientDAO.insertIngredient(newIngredient);
                    JOptionPane.showMessageDialog(dialog, "New ingredient '" + name + "' has been saved to the database!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Add the ingredient to the recipe
                RecipeIngredient ri = RecipeIngredient.builder()
                    .recipeId(0)
                    .ingredient(savedIngredient)
                    .quantity(quantity)
                    .unit(unit)
                    .build();
                
                ingredientsListModel.addElement(ri);
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save ingredient: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addNewButton, gbc);
        
        gbc.gridy = 7;
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dialog.dispose());
        panel.add(cancelBtn, gbc);
        
        JScrollPane scrollPaneWrapper = new JScrollPane(panel);
        scrollPaneWrapper.setBorder(null);
        scrollPaneWrapper.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        dialog.add(scrollPaneWrapper);
        dialog.setVisible(true);
    }
    
    private void removeIngredient() {
        int selectedIndex = ingredientsList.getSelectedIndex();
        if (selectedIndex != -1) {
            RecipeIngredient ri = ingredientsListModel.get(selectedIndex);
            String ingredientName = ri.ingredient() != null ? ri.ingredient().name() : "this ingredient";
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Remove " + ingredientName + " from the recipe?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ingredientsListModel.remove(selectedIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to remove", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void loadRecipeData() {
        if (existingRecipe == null) return;
        
        titleField.setText(existingRecipe.title());
        descriptionArea.setText(existingRecipe.description());
        prepTimeSpinner.setValue(existingRecipe.prepTime());
        cookTimeSpinner.setValue(existingRecipe.cookTime());
        servingsSpinner.setValue(existingRecipe.servings());
        difficultyCombo.setSelectedItem(existingRecipe.difficulty());
        
        if (existingRecipe.ingredients() != null) {
            ingredientsListModel.clear();
            for (RecipeIngredient ri : existingRecipe.ingredients()) {
                ingredientsListModel.addElement(ri);
            }
        }
        
        if (existingRecipe.nutrition() != null) {
            Nutrition n = existingRecipe.nutrition();
            caloriesSpinner.setValue(n.calories());
            proteinSpinner.setValue(n.protein() != null ? n.protein().intValue() : 0);
            carbsSpinner.setValue(n.carbs() != null ? n.carbs().intValue() : 0);
            fatSpinner.setValue(n.fat() != null ? n.fat().intValue() : 0);
        }
        
        // Load existing image if any
        if (existingRecipe.photoPath() != null && !existingRecipe.photoPath().isEmpty()) {
            String path = existingRecipe.photoPath();
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                imgFile = new File(System.getProperty("user.dir"), path);
            }
            if (!imgFile.exists()) {
                imgFile = new File("images/recipes/" + new File(path).getName());
            }
            if (imgFile.exists()) {
                try {
                    BufferedImage img = ImageIO.read(imgFile);
                    if (img != null) {
                        int maxWidth = 350;
                        int maxHeight = 140;
                        int imgWidth = img.getWidth();
                        int imgHeight = img.getHeight();
                        
                        double ratio = Math.min((double) maxWidth / imgWidth, (double) maxHeight / imgHeight);
                        int scaledWidth = (int) (imgWidth * ratio);
                        int scaledHeight = (int) (imgHeight * ratio);
                        
                        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        imagePreviewLabel.setIcon(new ImageIcon(scaledImg));
                        imagePreviewLabel.setText("");
                        selectedImageFile = imgFile;
                        imagePath = existingRecipe.photoPath();
                    }
                } catch (IOException e) {
                    System.err.println("Failed to load existing image: " + e.getMessage());
                }
            }
        }
    }
    
    private String getRelativeImagePath(String absolutePath) {
        if (absolutePath == null) return null;
        
        String projectPath = System.getProperty("user.dir");
        if (absolutePath.startsWith(projectPath)) {
            String relative = absolutePath.substring(projectPath.length() + 1);
            return relative;
        }
        return absolutePath;
    }
    
    private void saveRecipe() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipe title is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int servings = (Integer) servingsSpinner.getValue();
        if (servings < 1) {
            JOptionPane.showMessageDialog(this, "Servings must be at least 1", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (ingredientsListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one ingredient", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsListModel.size(); i++) {
            ingredients.add(ingredientsListModel.get(i));
        }
        
        Nutrition nutrition = Nutrition.builder()
            .recipeId(0)
            .calories((Integer) caloriesSpinner.getValue())
            .protein(BigDecimal.valueOf((Integer) proteinSpinner.getValue()))
            .carbs(BigDecimal.valueOf((Integer) carbsSpinner.getValue()))
            .fat(BigDecimal.valueOf((Integer) fatSpinner.getValue()))
            .build();
        
        String finalImagePath = getRelativeImagePath(imagePath);
        
        try {
            if (isEditMode) {
                Recipe updatedRecipe = Recipe.builder()
                    .id(existingRecipe.id())
                    .title(title)
                    .description(descriptionArea.getText())
                    .prepTime((Integer) prepTimeSpinner.getValue())
                    .cookTime((Integer) cookTimeSpinner.getValue())
                    .servings(servings)
                    .difficulty((String) difficultyCombo.getSelectedItem())
                    .ingredients(ingredients)
                    .nutrition(nutrition)
                    .photoPath(finalImagePath)
                    .isDraft(false)
                    .build();
                
                boolean success = recipeService.updateRecipe(updatedRecipe);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Your recipe has been updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window instanceof JDialog) {
                        ((JDialog) window).dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update recipe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                int currentUserId = SessionManager.getInstance().getCurrentUserId();
                Recipe newRecipe = Recipe.builder()
                    .userId(currentUserId)
                    .title(title)
                    .description(descriptionArea.getText())
                    .prepTime((Integer) prepTimeSpinner.getValue())
                    .cookTime((Integer) cookTimeSpinner.getValue())
                    .servings(servings)
                    .difficulty((String) difficultyCombo.getSelectedItem())
                    .ingredients(ingredients)
                    .nutrition(nutrition)
                    .photoPath(finalImagePath)
                    .isDraft(false)
                    .build();
                
                recipeService.createRecipe(newRecipe);
                JOptionPane.showMessageDialog(this, "Your new recipe has been created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static class IngredientListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                       boolean isSelected, boolean cellHasFocus) {
            if (value instanceof RecipeIngredient ri) {
                String name = ri.ingredient() != null ? ri.ingredient().name() : "Unknown";
                value = name + " - " + ri.getFormattedQuantity();
            }
            
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (isSelected) {
                c.setBackground(new Color(52, 152, 219));
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
                c.setForeground(ThemeManager.getInstance().getTextColor());
            }
            
            return c;
        }
    }
}