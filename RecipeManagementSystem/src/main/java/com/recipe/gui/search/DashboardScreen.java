package com.recipe.gui.search;

import com.recipe.models.DashboardStats;
import com.recipe.services.DashboardService;
import com.recipe.gui.recipe.RecipeCard;
import com.recipe.gui.components.ToastNotifier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DashboardScreen extends JPanel {  // Changed from JFrame to JPanel
    
    private final DashboardService dashboardService;
    private JLabel totalRecipesLabel;
    private JLabel avgRatingLabel;
    private JLabel totalCookLabel;
    private JLabel weeklyPlansLabel;
    private JPanel topRatedPanel;
    private JPanel mostCookedPanel;
    
    public DashboardScreen() {
        this.dashboardService = new DashboardService();
        initComponents();
        loadDashboardData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📊 Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 134, 222));
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Stats Grid
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Row 1 - Statistics Cards
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.25;
        panel.add(createStatCard("📖 Total Recipes", totalRecipesLabel = new JLabel("0"), new Color(52, 152, 219)), gbc);
        
        gbc.gridx = 1;
        panel.add(createStatCard("⭐ Avg Rating", avgRatingLabel = new JLabel("0.0"), new Color(241, 196, 15)), gbc);
        
        gbc.gridx = 2;
        panel.add(createStatCard("🍳 Times Cooked", totalCookLabel = new JLabel("0"), new Color(46, 204, 113)), gbc);
        
        gbc.gridx = 3;
        panel.add(createStatCard("📅 Weekly Plans", weeklyPlansLabel = new JLabel("0"), new Color(155, 89, 182)), gbc);
        
        // Row 2 - Top Rated Recipes
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        panel.add(createTopRatedPanel(), gbc);
        
        // Row 2 - Most Cooked Recipes
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        panel.add(createMostCookedPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTopRatedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "⭐ Top Rated Recipes", TitledBorder.LEFT, TitledBorder.TOP));
        
        topRatedPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        topRatedPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(topRatedPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMostCookedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "🔥 Most Cooked Recipes", TitledBorder.LEFT, TitledBorder.TOP));
        
        mostCookedPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mostCookedPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(mostCookedPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadDashboardData() {
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                return dashboardService.getDashboardStats();
            }
            
            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();
                    updateUI(stats);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateUI(DashboardStats stats) {
        totalRecipesLabel.setText(String.valueOf(stats.totalRecipes()));
        avgRatingLabel.setText(String.format("%.1f", stats.averageRating()));
        totalCookLabel.setText(String.valueOf(stats.totalCookCount()));
        weeklyPlansLabel.setText(String.valueOf(stats.thisWeekPlans()));
        
        topRatedPanel.removeAll();
        if (stats.topRatedRecipe() != null) {
            RecipeCard card = new RecipeCard(stats.topRatedRecipe());
            topRatedPanel.add(card);
        } else {
            topRatedPanel.add(new JLabel("No ratings yet"));
        }
        
        mostCookedPanel.removeAll();
        if (stats.mostCookedRecipe() != null) {
            RecipeCard card = new RecipeCard(stats.mostCookedRecipe());
            mostCookedPanel.add(card);
        } else {
            mostCookedPanel.add(new JLabel("No cooking history yet"));
        }
        
        topRatedPanel.revalidate();
        topRatedPanel.repaint();
        mostCookedPanel.revalidate();
        mostCookedPanel.repaint();
    }
}