package com.recipe.gui.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.recipe.gui.components.ToastNotifier;
import com.recipe.gui.recipe.RecipeCard;
import com.recipe.gui.recipe.RecipeDetailScreen;
import com.recipe.models.Recipe;
import com.recipe.models.SearchFilter;
import com.recipe.services.FilterEngine;

public class SearchScreen extends JPanel {
    
    private final FilterEngine filterEngine;
    private JTextField searchField;
    private JSlider calorieSlider;
    private JSlider timeSlider;
    private JComboBox<String> difficultyCombo;
    private JComboBox<String> categoryCombo;
    private JSpinner minRatingSpinner;
    private JCheckBox favouriteOnlyCheckBox;
    private JButton searchButton;
    private JButton resetButton;
    private JPanel resultsPanel;
    private JLabel resultsCountLabel;
    
    public SearchScreen() {
        this.filterEngine = new FilterEngine();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.WEST);
        
        // Results Panel
        JPanel resultsContainer = createResultsContainer();
        add(resultsContainer, BorderLayout.CENTER);
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(280, 600));
        
        // Search Box
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new TitledBorder("Search"));
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(searchField, BorderLayout.CENTER);
        panel.add(searchPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Calories Filter
        JPanel caloriesPanel = new JPanel(new BorderLayout());
        caloriesPanel.setBorder(new TitledBorder("Max Calories"));
        calorieSlider = new JSlider(0, 2000, 1000);
        calorieSlider.setMajorTickSpacing(500);
        calorieSlider.setPaintTicks(true);
        calorieSlider.setPaintLabels(true);
        caloriesPanel.add(calorieSlider, BorderLayout.CENTER);
        panel.add(caloriesPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Time Filter
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBorder(new TitledBorder("Max Time (minutes)"));
        timeSlider = new JSlider(0, 180, 60);
        timeSlider.setMajorTickSpacing(30);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timePanel.add(timeSlider, BorderLayout.CENTER);
        panel.add(timePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Difficulty
        JPanel difficultyPanel = new JPanel(new BorderLayout());
        difficultyPanel.setBorder(new TitledBorder("Difficulty"));
        difficultyCombo = new JComboBox<>(new String[]{"Any", "Easy", "Medium", "Hard"});
        difficultyPanel.add(difficultyCombo, BorderLayout.CENTER);
        panel.add(difficultyPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Category
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(new TitledBorder("Category"));
        categoryCombo = new JComboBox<>(new String[]{"Any", "Italian", "Chinese", "Mexican", "Indian", "Breakfast", "Dinner"});
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        panel.add(categoryPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Rating
        JPanel ratingPanel = new JPanel(new BorderLayout());
        ratingPanel.setBorder(new TitledBorder("Minimum Rating"));
        minRatingSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5, 0.5));
        ratingPanel.add(minRatingSpinner, BorderLayout.CENTER);
        panel.add(ratingPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Favourites only
        favouriteOnlyCheckBox = new JCheckBox("Show only my favourites");
        panel.add(favouriteOnlyCheckBox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchButton = new JButton("🔍 Search");
        searchButton.setBackground(new Color(46, 204, 113));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        
        resetButton = new JButton("Reset Filters");
        resetButton.addActionListener(e -> resetFilters());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createResultsContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Search Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        resultsCountLabel = new JLabel("0 recipes found");
        resultsCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultsCountLabel.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(resultsCountLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Results Grid
        resultsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        resultsPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void performSearch() {
        resultsPanel.removeAll();
        
        SearchFilter filter = SearchFilter.builder()
            .searchTerm(searchField.getText().trim().isEmpty() ? null : searchField.getText().trim())
            .maxCalories(calorieSlider.getValue())
            .maxCookTime(timeSlider.getValue())
            .difficulty(difficultyCombo.getSelectedIndex() == 0 ? null : (String) difficultyCombo.getSelectedItem())
            .category(categoryCombo.getSelectedIndex() == 0 ? null : (String) categoryCombo.getSelectedItem())
            .minRating((Double) minRatingSpinner.getValue() > 0 ? (Double) minRatingSpinner.getValue() : null)
            .build();
        
        SwingWorker<List<Recipe>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Recipe> doInBackground() throws Exception {
                return filterEngine.searchWithFilters(filter);
            }
            
            @Override
            protected void done() {
                try {
                    List<Recipe> results = get();
                    displayResults(results);
                    resultsCountLabel.setText(results.size() + " recipes found");
                    
                    if (results.isEmpty()) {
                        JLabel emptyLabel = new JLabel("No recipes match your search criteria");
                        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                        emptyLabel.setForeground(new Color(150, 150, 150));
                        resultsPanel.add(emptyLabel);
                    }
                    
                } catch (Exception e) {
                    ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(SearchScreen.this),
                        "Search failed: " + e.getMessage(), ToastNotifier.ERROR);
                }
            }
        };
        worker.execute();
    }
    
    private void displayResults(List<Recipe> recipes) {
        resultsPanel.removeAll();
        resultsPanel.setLayout(new GridLayout(0, 2, 15, 15));
        
        for (Recipe recipe : recipes) {
            RecipeCard card = new RecipeCard(recipe);
            card.setOnClickCallback(() -> openRecipeDetail(recipe));
            resultsPanel.add(card);
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
    
    private void openRecipeDetail(Recipe recipe) {
        RecipeDetailScreen detailScreen = new RecipeDetailScreen(recipe);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(parentFrame, "Recipe Details", true);
        dialog.setContentPane(detailScreen);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void resetFilters() {
        searchField.setText("");
        calorieSlider.setValue(1000);
        timeSlider.setValue(60);
        difficultyCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        minRatingSpinner.setValue(0);
        favouriteOnlyCheckBox.setSelected(false);
        performSearch();
    }
}