package com.recipe.gui.mealplan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.recipe.models.DailyNutritionSummary;
import com.recipe.services.MealPlanService;

public class NutritionPanel extends JPanel {
    
    private final MealPlanService mealPlanService;
    private LocalDate weekStart;
    private JPanel weekSummaryPanel;
    private JLabel weeklyTotalLabel;
    
    public NutritionPanel() {
        this.mealPlanService = new MealPlanService();
        this.weekStart = getStartOfWeek(LocalDate.now());
        initComponents();
        loadNutritionData();
    }
    
    private LocalDate getStartOfWeek(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("📊 Weekly Nutrition Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(46, 134, 222));
        add(titleLabel, BorderLayout.NORTH);
        
        weekSummaryPanel = new JPanel();
        weekSummaryPanel.setLayout(new BoxLayout(weekSummaryPanel, BoxLayout.Y_AXIS));
        weekSummaryPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(weekSummaryPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        weeklyTotalLabel = new JLabel();
        weeklyTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        footerPanel.add(weeklyTotalLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void loadNutritionData() {
        weekSummaryPanel.removeAll();
        
        SwingWorker<Map<LocalDate, DailyNutritionSummary>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<LocalDate, DailyNutritionSummary> doInBackground() throws Exception {
                return mealPlanService.getWeeklyNutritionSummary(weekStart);
            }
            
            @Override
            protected void done() {
                try {
                    Map<LocalDate, DailyNutritionSummary> summaries = get();
                    int weeklyTotal = 0;
                    
                    for (int i = 0; i < 7; i++) {
                        LocalDate date = weekStart.plusDays(i);
                        DailyNutritionSummary summary = summaries.getOrDefault(date, 
                            DailyNutritionSummary.builder().date(date).calorieGoal(2000).build());
                        
                        weekSummaryPanel.add(createDayPanel(summary));
                        weekSummaryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        
                        weeklyTotal += summary.totalCalories();
                    }
                    
                    weeklyTotalLabel.setText("Weekly Total: " + weeklyTotal + " kcal");
                    
                } catch (Exception e) {
                    JLabel errorLabel = new JLabel("Failed to load nutrition data: " + e.getMessage());
                    errorLabel.setForeground(Color.RED);
                    weekSummaryPanel.add(errorLabel);
                }
                
                weekSummaryPanel.revalidate();
                weekSummaryPanel.repaint();
            }
        };
        worker.execute();
    }
    
    private JPanel createDayPanel(DailyNutritionSummary summary) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Day header
        JLabel dayLabel = new JLabel(summary.date().format(DateTimeFormatter.ofPattern("EEEE, MMM d")));
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(dayLabel, BorderLayout.NORTH);
        
        // Calories row
        JPanel caloriesPanel = new JPanel(new BorderLayout());
        caloriesPanel.setBackground(new Color(250, 250, 250));
        
        JLabel caloriesLabel = new JLabel("Calories: " + summary.totalCalories() + " / " + summary.calorieGoal() + " kcal");
        caloriesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(summary.getCaloriePercentage());
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        Color progressColor = summary.isGoalMet() ? new Color(46, 204, 113) : new Color(231, 76, 60);
        progressBar.setForeground(progressColor);
        progressBar.setPreferredSize(new Dimension(200, 15));
        
        caloriesPanel.add(caloriesLabel, BorderLayout.WEST);
        caloriesPanel.add(progressBar, BorderLayout.EAST);
        
        panel.add(caloriesPanel, BorderLayout.CENTER);
        
        // Macros row
        JPanel macrosPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        macrosPanel.setBackground(new Color(250, 250, 250));
        macrosPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        macrosPanel.add(createMacroLabel("Protein", summary.totalProtein().doubleValue(), summary.getProteinPercentage(), new Color(46, 204, 113)));
        macrosPanel.add(createMacroLabel("Carbs", summary.totalCarbs().doubleValue(), summary.getCarbsPercentage(), new Color(52, 152, 219)));
        macrosPanel.add(createMacroLabel("Fat", summary.totalFat().doubleValue(), summary.getFatPercentage(), new Color(241, 196, 15)));
        
        panel.add(macrosPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMacroLabel(String name, double grams, double percentage, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nameLabel.setForeground(color);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(String.format("%.1f g", grams));
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel percentLabel = new JLabel(String.format("(%.0f%%)", percentage));
        percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        percentLabel.setForeground(new Color(120, 120, 120));
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(nameLabel);
        panel.add(valueLabel);
        panel.add(percentLabel);
        
        return panel;
    }
    
    public void refresh() {
        loadNutritionData();
    }
}