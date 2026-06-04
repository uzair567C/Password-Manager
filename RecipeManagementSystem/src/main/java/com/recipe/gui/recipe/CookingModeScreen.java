package com.recipe.gui.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;

public class CookingModeScreen extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final Recipe recipe;
    private int currentStep = 0;
    private Timer timer;
    private int remainingSeconds;
    private JLabel stepLabel;
    private JLabel timerLabel;
    private JTextArea stepTextArea;
    private JButton prevButton;
    private JButton nextButton;
    private JButton startTimerButton;
    private JButton stopTimerButton;
    private JLabel ingredientListLabel;
    private JProgressBar progressBar;
    
    public CookingModeScreen(Recipe recipe) {
        this.recipe = recipe;
        initComponents();
        loadStep(0);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(50, 50, 50));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("👨‍🍳 Cooking Mode: " + recipe.title());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(200, 10));
        headerPanel.add(progressBar, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        
        JPanel ingredientsPanel = createIngredientsPanel();
        centerPanel.add(ingredientsPanel);
        
        JPanel stepsPanel = createStepsPanel();
        centerPanel.add(stepsPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 60, 60));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("📋 Ingredients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        StringBuilder ingredients = new StringBuilder();
        for (RecipeIngredient ri : recipe.ingredients()) {
            String name = ri.ingredient() != null ? ri.ingredient().name() : "Unknown";
            ingredients.append("• ").append(name)
                .append(": ").append(ri.getFormattedQuantity()).append("\n");
        }
        
        ingredientListLabel = new JLabel(ingredients.toString());
        ingredientListLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ingredientListLabel.setForeground(new Color(220, 220, 220));
        ingredientListLabel.setVerticalAlignment(SwingConstants.TOP);
        
        JScrollPane scrollPane = new JScrollPane(ingredientListLabel);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStepsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 60, 60));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel stepHeader = new JPanel(new BorderLayout());
        stepHeader.setOpaque(false);
        
        stepLabel = new JLabel("Step 1");
        stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stepLabel.setForeground(new Color(46, 204, 113));
        stepHeader.add(stepLabel, BorderLayout.WEST);
        
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        timerPanel.setOpaque(false);
        
        startTimerButton = new JButton("▶ Start Timer");
        startTimerButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        startTimerButton.setBackground(new Color(46, 204, 113));
        startTimerButton.setForeground(Color.WHITE);
        startTimerButton.setFocusPainted(false);
        startTimerButton.addActionListener(e -> startTimer());
        
        stopTimerButton = new JButton("■ Stop");
        stopTimerButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stopTimerButton.setBackground(new Color(231, 76, 60));
        stopTimerButton.setForeground(Color.WHITE);
        stopTimerButton.setFocusPainted(false);
        stopTimerButton.addActionListener(e -> stopTimer());
        stopTimerButton.setEnabled(false);
        
        timerLabel = new JLabel("No timer");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerLabel.setForeground(new Color(241, 196, 15));
        
        timerPanel.add(timerLabel);
        timerPanel.add(startTimerButton);
        timerPanel.add(stopTimerButton);
        
        stepHeader.add(timerPanel, BorderLayout.EAST);
        
        panel.add(stepHeader, BorderLayout.NORTH);
        
        stepTextArea = new JTextArea();
        stepTextArea.setEditable(false);
        stepTextArea.setLineWrap(true);
        stepTextArea.setWrapStyleWord(true);
        stepTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        stepTextArea.setBackground(new Color(70, 70, 70));
        stepTextArea.setForeground(Color.WHITE);
        stepTextArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(stepTextArea);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setOpaque(false);
        
        prevButton = new JButton("← Previous Step");
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevButton.setBackground(new Color(100, 100, 100));
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(e -> previousStep());
        
        nextButton = new JButton("Next Step →");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextButton.setBackground(new Color(46, 204, 113));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> nextStep());
        
        JButton exitButton = new JButton("Exit Cooking Mode");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> {
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            dialog.dispose();
        });
        
        panel.add(prevButton);
        panel.add(nextButton);
        panel.add(exitButton);
        
        return panel;
    }
    
    private void loadStep(int stepIndex) {
        String[] steps = getSampleSteps();
        
        if (steps == null || steps.length == 0) {
            stepTextArea.setText("No steps defined for this recipe.\n\nFollow your own instructions!");
            stepLabel.setText("Instructions");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            progressBar.setValue(100);
            return;
        }
        
        stepLabel.setText("Step " + (stepIndex + 1) + " of " + steps.length);
        stepTextArea.setText(steps[stepIndex]);
        
        prevButton.setEnabled(stepIndex > 0);
        nextButton.setEnabled(stepIndex < steps.length - 1);
        
        int progress = (int) (((double) (stepIndex + 1) / steps.length) * 100);
        progressBar.setValue(progress);
        
        stopTimer();
        remainingSeconds = 0;
        timerLabel.setText("No timer");
    }
    
    private String[] getSampleSteps() {
        return new String[]{
            "1. Prepare all ingredients and equipment.\n\n• Wash and chop vegetables\n• Measure all ingredients\n• Preheat oven if needed",
            "2. Follow the main cooking instructions for this recipe.\n\nCook according to your recipe's specific directions.",
            "3. Final plating and presentation.\n\n• Garnish as desired\n• Serve hot and enjoy!"
        };
    }
    
    private void nextStep() {
        currentStep++;
        loadStep(currentStep);
    }
    
    private void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            loadStep(currentStep);
        }
    }
    
    private void startTimer() {
        if (remainingSeconds <= 0) {
            String input = JOptionPane.showInputDialog(this, 
                "Enter timer duration (seconds):", "Set Timer", JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    remainingSeconds = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    return;
                }
            } else {
                return;
            }
        }
        
        if (timer != null) {
            timer.stop();
        }
        
        startTimerButton.setEnabled(false);
        stopTimerButton.setEnabled(true);
        
        timer = new Timer(1000, (ActionEvent e) -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                int minutes = remainingSeconds / 60;
                int seconds = remainingSeconds % 60;
                timerLabel.setText(String.format("⏰ %02d:%02d", minutes, seconds));
            } else {
                stopTimer();
                timerLabel.setText("✓ Time's up!");
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Timer finished!", "Time's Up", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        timer.start();
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        startTimerButton.setEnabled(true);
        stopTimerButton.setEnabled(false);
    }
}