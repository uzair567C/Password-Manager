package com.recipe.gui.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.recipe.auth.SessionManager;
import com.recipe.gui.ThemeManager;
import com.recipe.gui.components.SearchBar;
import com.recipe.models.Recipe;
import com.recipe.services.RecipeService;

public class RecipeListScreen extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final RecipeService recipeService;
    private JPanel recipesGridPanel;
    private JScrollPane scrollPane;
    private JLabel loadingLabel;
    private JButton addRecipeButton;
    private JButton refreshButton;
    private SearchBar searchBar;
    private JComboBox<String> sortComboBox;
    private List<Recipe> currentRecipes;
    private JLabel userStatsLabel;
    
    public RecipeListScreen() {
        this.recipeService = new RecipeService();
        initComponents();
        loadMyRecipes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        recipesGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        recipesGridPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        
        scrollPane = new JScrollPane(recipesGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeManager.getInstance().getBackgroundColor());
        scrollPane.getViewport().setBackground(ThemeManager.getInstance().getBackgroundColor());
        
        loadingLabel = new JLabel("Loading your recipes...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        loadingLabel.setForeground(ThemeManager.getInstance().getMutedTextColor());
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getInstance().getBorderColor()),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        
        JLabel titleLabel = new JLabel("My Recipe Collection");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ThemeManager.getInstance().getPrimaryColor());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        String username = SessionManager.getInstance().getCurrentUser().getUsername();
        userStatsLabel = new JLabel("👨‍🍳 " + username + "'s personal recipes");
        userStatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userStatsLabel.setForeground(ThemeManager.getInstance().getMutedTextColor());
        titlePanel.add(userStatsLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(ThemeManager.getInstance().getCardBackgroundColor());
        
        searchBar = new SearchBar();
        searchBar.setPreferredSize(new Dimension(250, 35));
        searchBar.addSearchListener(searchTerm -> filterMyRecipes(searchTerm));
        actionsPanel.add(searchBar);
        
        sortComboBox = new JComboBox<>(new String[]{"Latest First", "Oldest First", "Most Cooked", "Top Rated", "Name A-Z"});
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sortComboBox.setPreferredSize(new Dimension(120, 30));
        sortComboBox.addActionListener(e -> sortRecipes());
        actionsPanel.add(sortComboBox);
        
        refreshButton = new JButton("🔄");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        refreshButton.setBackground(new Color(240, 240, 240));
        refreshButton.setForeground(new Color(80, 80, 80));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Refresh");
        refreshButton.setPreferredSize(new Dimension(35, 35));
        refreshButton.addActionListener(e -> loadMyRecipes());
        actionsPanel.add(refreshButton);
        
        addRecipeButton = new JButton("+");
        addRecipeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        addRecipeButton.setBackground(new Color(46, 204, 113));
        addRecipeButton.setForeground(Color.WHITE);
        addRecipeButton.setFocusPainted(false);
        addRecipeButton.setBorderPainted(false);
        addRecipeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addRecipeButton.setToolTipText("Add New Recipe");
        addRecipeButton.setPreferredSize(new Dimension(35, 35));
        addRecipeButton.addActionListener(e -> openAddRecipeScreen());
        actionsPanel.add(addRecipeButton);
        
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void loadMyRecipes() {
        showLoading(true);
        
        SwingWorker<List<Recipe>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Recipe> doInBackground() throws Exception {
                return recipeService.getMyRecipes();
            }
            
            @Override
            protected void done() {
                try {
                    currentRecipes = get();
                    displayRecipes(currentRecipes);
                    String username = SessionManager.getInstance().getCurrentUser().getUsername();
                    userStatsLabel.setText("👨‍🍳 " + username + "'s personal recipes (" + currentRecipes.size() + " recipes)");
                } catch (InterruptedException | ExecutionException e) {
                    showError("Failed to load recipes: " + e.getMessage());
                } finally {
                    showLoading(false);
                }
            }
        };
        worker.execute();
    }
    
    private void displayRecipes(List<Recipe> recipes) {
        recipesGridPanel.removeAll();
        
        if (recipes == null || recipes.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
            
            JLabel emptyLabel = new JLabel("You haven't created any recipes yet. Click + to add your first recipe!");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(ThemeManager.getInstance().getMutedTextColor());
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel emptyIcon = new JLabel("🍽️");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyIcon.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.add(emptyIcon, BorderLayout.NORTH);
            textPanel.add(emptyLabel, BorderLayout.CENTER);
            
            emptyPanel.add(textPanel, BorderLayout.CENTER);
            recipesGridPanel.setLayout(new BorderLayout());
            recipesGridPanel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            recipesGridPanel.setLayout(new GridLayout(0, 3, 15, 15));
            
            for (Recipe recipe : recipes) {
                RecipeCard card = new RecipeCard(recipe);
                card.setOnClickCallback(() -> openRecipeDetail(recipe));
                recipesGridPanel.add(card);
            }
        }
        
        recipesGridPanel.revalidate();
        recipesGridPanel.repaint();
    }
    
    private void sortRecipes() {
        if (currentRecipes == null) return;
        
        String sortBy = (String) sortComboBox.getSelectedItem();
        
        if (sortBy != null) {
            switch (sortBy) {
                case "Latest First":
                    currentRecipes.sort((r1, r2) -> {
                        if (r1.createdAt() == null && r2.createdAt() == null) return 0;
                        if (r1.createdAt() == null) return 1;
                        if (r2.createdAt() == null) return -1;
                        return r2.createdAt().compareTo(r1.createdAt());
                    });
                    break;
                case "Oldest First":
                    currentRecipes.sort((r1, r2) -> {
                        if (r1.createdAt() == null && r2.createdAt() == null) return 0;
                        if (r1.createdAt() == null) return 1;
                        if (r2.createdAt() == null) return -1;
                        return r1.createdAt().compareTo(r2.createdAt());
                    });
                    break;
                case "Most Cooked":
                    currentRecipes.sort((r1, r2) -> Integer.compare(r2.cookCount(), r1.cookCount()));
                    break;
                case "Top Rated":
                    currentRecipes.sort((r1, r2) -> Double.compare(r2.averageRating(), r1.averageRating()));
                    break;
                case "Name A-Z":
                    currentRecipes.sort((r1, r2) -> r1.title().compareToIgnoreCase(r2.title()));
                    break;
                default:
                    break;
            }
        }
        
        displayRecipes(currentRecipes);
    }
    
    private void filterMyRecipes(String searchTerm) {
        if (currentRecipes == null) return;
        
        if (searchTerm == null || searchTerm.isBlank()) {
            displayRecipes(currentRecipes);
            return;
        }
        
        List<Recipe> filtered = currentRecipes.stream()
            .filter(recipe -> recipe.title().toLowerCase().contains(searchTerm.toLowerCase()) ||
                              (recipe.description() != null && recipe.description().toLowerCase().contains(searchTerm.toLowerCase())))
            .toList();
        
        displayRecipes(filtered);
    }
    
    private void openRecipeDetail(Recipe recipe) {
        RecipeDetailScreen detailScreen = new RecipeDetailScreen(recipe);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(parentFrame, "Recipe Details", true);
        dialog.setContentPane(detailScreen);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        
        detailScreen.addOnCloseListener(() -> loadMyRecipes());
    }
    
    private void openAddRecipeScreen() {
        AddEditRecipeScreen addScreen = new AddEditRecipeScreen(null);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(parentFrame, "Add New Recipe", true);
        dialog.setContentPane(addScreen);
        dialog.setSize(1000, 800);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
        
        loadMyRecipes();
    }
    
    private void showLoading(boolean show) {
        if (show) {
            scrollPane.setViewportView(loadingLabel);
        } else {
            scrollPane.setViewportView(recipesGridPanel);
        }
        refreshButton.setEnabled(!show);
        addRecipeButton.setEnabled(!show);
        searchBar.setEnabled(!show);
        sortComboBox.setEnabled(!show);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        showLoading(false);
    }
}