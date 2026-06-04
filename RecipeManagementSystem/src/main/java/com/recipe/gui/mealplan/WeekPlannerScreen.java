package com.recipe.gui.mealplan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.recipe.gui.components.ToastNotifier;
import com.recipe.models.MealPlan;
import com.recipe.models.Recipe;
import com.recipe.services.MealPlanService;
import com.recipe.services.RecipeService;

public class WeekPlannerScreen extends JPanel {
    
    private final MealPlanService mealPlanService;
    private final RecipeService recipeService;
    private LocalDate currentWeekStart;
    private JLabel weekRangeLabel;
    private JPanel plannerGridPanel;
    private JButton prevWeekButton;
    private JButton nextWeekButton;
    private JButton clearWeekButton;
    private JButton generateGroceryButton;
    private Map<String, JButton> mealButtons;
    
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String[] MEAL_TYPES = {"Breakfast", "Lunch", "Dinner", "Snack"};
    
    public WeekPlannerScreen() {
        this.mealPlanService = new MealPlanService();
        this.recipeService = new RecipeService();
        this.currentWeekStart = getStartOfWeek(LocalDate.now());
        this.mealButtons = new HashMap<>();
        initComponents();
        loadWeekPlan();
    }
    
    private LocalDate getStartOfWeek(LocalDate date) {
        // Adjust to Monday (1) instead of Sunday (7)
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Planner Grid
        plannerGridPanel = createPlannerGrid();
        JScrollPane scrollPane = new JScrollPane(plannerGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer
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
        
        JLabel titleLabel = new JLabel("📅 Weekly Meal Planner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 134, 222));
        
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        navPanel.setBackground(Color.WHITE);
        
        prevWeekButton = new JButton("◀ Previous Week");
        prevWeekButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevWeekButton.addActionListener(e -> navigateWeek(-1));
        
        weekRangeLabel = new JLabel();
        weekRangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        weekRangeLabel.setForeground(new Color(80, 80, 80));
        
        nextWeekButton = new JButton("Next Week ▶");
        nextWeekButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextWeekButton.addActionListener(e -> navigateWeek(1));
        
        navPanel.add(prevWeekButton);
        navPanel.add(weekRangeLabel);
        navPanel.add(nextWeekButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(navPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPlannerGrid() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Header row (days)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Empty corner cell
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(100, 40));
        panel.add(cornerLabel, gbc);
        
        // Day headers
        for (int i = 0; i < DAYS.length; i++) {
            gbc.gridx = i + 1;
            JPanel dayHeader = createDayHeader(DAYS[i], currentWeekStart.plusDays(i));
            panel.add(dayHeader, gbc);
        }
        
        // Meal rows
        for (int mealIdx = 0; mealIdx < MEAL_TYPES.length; mealIdx++) {
            gbc.gridy = mealIdx + 1;
            
            // Meal type label
            gbc.gridx = 0;
            JLabel mealLabel = new JLabel(MEAL_TYPES[mealIdx]);
            mealLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            mealLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mealLabel.setPreferredSize(new Dimension(100, 80));
            panel.add(mealLabel, gbc);
            
            // Meal buttons for each day
            for (int dayIdx = 0; dayIdx < DAYS.length; dayIdx++) {
                gbc.gridx = dayIdx + 1;
                String key = dayIdx + "_" + MEAL_TYPES[mealIdx];
                JButton mealButton = createMealButton(dayIdx, MEAL_TYPES[mealIdx]);
                mealButtons.put(key, mealButton);
                panel.add(mealButton, gbc);
            }
        }
        
        return panel;
    }
    
    private JPanel createDayHeader(String dayName, LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        panel.setPreferredSize(new Dimension(120, 60));
        
        JLabel dayLabel = new JLabel(dayName);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dayLabel.setForeground(Color.WHITE);
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dateLabel = new JLabel(date.format(DateTimeFormatter.ofPattern("MMM d")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(new Color(240, 240, 240));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(dayLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(dateLabel);
        
        return panel;
    }
    
    private JButton createMealButton(int dayOfWeek, String mealType) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(120, 70));
        button.setBackground(new Color(250, 250, 250));
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        button.addActionListener(e -> {
            showRecipePicker(dayOfWeek, mealType);
        });
        
        return button;
    }
    
    private void showRecipePicker(int dayOfWeek, String mealType) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Select Recipe", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Select a recipe for " + mealType + " on " + DAYS[dayOfWeek]);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Recipe list
        DefaultListModel<Recipe> listModel = new DefaultListModel<>();
        JList<Recipe> recipeList = new JList<>(listModel);
        recipeList.setCellRenderer(new RecipeListRenderer());
        
        try {
            List<Recipe> recipes = recipeService.getAllRecipes();
            for (Recipe recipe : recipes) {
                listModel.addElement(recipe);
            }
        } catch (Exception e) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Failed to load recipes: " + e.getMessage(), ToastNotifier.ERROR);
        }
        
        JScrollPane scrollPane = new JScrollPane(recipeList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton assignButton = new JButton("Assign to Meal");
        JButton cancelButton = new JButton("Cancel");
        JButton clearButton = new JButton("Clear Meal");
        
        assignButton.addActionListener(e -> {
            Recipe selected = recipeList.getSelectedValue();
            if (selected != null) {
                assignMeal(dayOfWeek, mealType, selected.id());
                dialog.dispose();
            } else {
                ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Please select a recipe", ToastNotifier.WARNING);
            }
        });
        
        clearButton.addActionListener(e -> {
            clearMeal(dayOfWeek, mealType);
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(assignButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void assignMeal(int dayOfWeek, String mealType, int recipeId) {
        try {
            mealPlanService.assignRecipeToMeal(recipeId, dayOfWeek, mealType, currentWeekStart);
            loadWeekPlan();
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Meal assigned successfully!", ToastNotifier.SUCCESS);
        } catch (Exception e) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Failed to assign meal: " + e.getMessage(), ToastNotifier.ERROR);
        }
    }
    
    private void clearMeal(int dayOfWeek, String mealType) {
        try {
            mealPlanService.removeMealPlan(dayOfWeek, mealType, currentWeekStart);
            loadWeekPlan();
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Meal cleared!", ToastNotifier.SUCCESS);
        } catch (Exception e) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Failed to clear meal: " + e.getMessage(), ToastNotifier.ERROR);
        }
    }
    
    private void loadWeekPlan() {
        try {
            List<MealPlan> weekPlans = mealPlanService.getWeekPlan(currentWeekStart);
            
            // Clear all buttons
            for (JButton button : mealButtons.values()) {
                button.setText("");
                button.setBackground(new Color(250, 250, 250));
            }
            
            // Populate buttons with meal plans
            for (MealPlan plan : weekPlans) {
                String key = plan.dayOfWeek() + "_" + plan.mealType();
                JButton button = mealButtons.get(key);
                if (button != null) {
                    button.setText("<html><center>" + plan.recipeTitle() + "</center></html>");
                    button.setBackground(new Color(200, 230, 200));
                }
            }
            
            // Update week range label
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            weekRangeLabel.setText(currentWeekStart.format(DateTimeFormatter.ofPattern("MMM d")) + 
                " - " + weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
            
        } catch (Exception e) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Failed to load meal plan: " + e.getMessage(), ToastNotifier.ERROR);
        }
    }
    
    private void navigateWeek(int delta) {
        currentWeekStart = currentWeekStart.plusWeeks(delta);
        loadWeekPlan();
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        clearWeekButton = new JButton("🗑️ Clear Entire Week");
        clearWeekButton.setBackground(new Color(231, 76, 60));
        clearWeekButton.setForeground(Color.WHITE);
        clearWeekButton.setFocusPainted(false);
        clearWeekButton.addActionListener(e -> clearEntireWeek());
        
        generateGroceryButton = new JButton("🛒 Generate Grocery List");
        generateGroceryButton.setBackground(new Color(46, 204, 113));
        generateGroceryButton.setForeground(Color.WHITE);
        generateGroceryButton.setFocusPainted(false);
        generateGroceryButton.addActionListener(e -> openGroceryList());
        
        panel.add(clearWeekButton);
        panel.add(generateGroceryButton);
        
        return panel;
    }
    
    private void clearEntireWeek() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all meals for this week?",
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                mealPlanService.clearWeekPlan(currentWeekStart);
                loadWeekPlan();
                ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Week plan cleared!", ToastNotifier.SUCCESS);
            } catch (Exception e) {
                ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Failed to clear: " + e.getMessage(), ToastNotifier.ERROR);
            }
        }
    }
    
    private void openGroceryList() {
        GroceryListScreen groceryScreen = new GroceryListScreen(currentWeekStart);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(parentFrame, "Grocery List", true);
        dialog.setContentPane(groceryScreen);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private static class RecipeListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Recipe recipe) {
                value = recipe.title() + " (⭐ " + String.format("%.1f", recipe.averageRating()) + ")";
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}