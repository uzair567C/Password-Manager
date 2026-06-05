package com.recipe.gui.recipe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.recipe.models.Recipe;

public class RecipeCard extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final Recipe recipe;
    private Runnable onClickCallback;
    private JLabel imageLabel;
    private String currentImagePath;
    
    public RecipeCard(Recipe recipe) {
        this.recipe = recipe;
        this.currentImagePath = recipe.photoPath();
        initComponents();
        loadImage();
    }
    
    public void setOnClickCallback(Runnable callback) {
        this.onClickCallback = callback;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(280, 320));
        setMaximumSize(new Dimension(280, 320));
        setMinimumSize(new Dimension(260, 300));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(250, 250, 250));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.WHITE);
                setCursor(Cursor.getDefaultCursor());
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClickCallback != null) {
                    onClickCallback.run();
                }
            }
        });
        
        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(260, 140));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        add(imagePanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel titleLabel = new JLabel(recipe.title());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        String difficulty = recipe.difficulty();
        String difficultyText = difficulty != null ? difficulty : "Medium";
        JLabel difficultyLabel = new JLabel(getDifficultyIcon(difficultyText) + " " + difficultyText);
        difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel timeLabel = new JLabel("⏱️ " + recipe.getFormattedTotalTime());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(100, 100, 100));
        
        JLabel servingsLabel = new JLabel("🍽️ " + recipe.servings() + " servings");
        servingsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        servingsLabel.setForeground(new Color(100, 100, 100));
        
        JLabel ratingLabel = new JLabel(createRatingStars(recipe.averageRating()));
        ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        detailsPanel.add(difficultyLabel);
        detailsPanel.add(timeLabel);
        detailsPanel.add(servingsLabel);
        detailsPanel.add(ratingLabel);
        
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        
        JLabel costLabel = new JLabel("💰 $" + String.format("%.2f", recipe.cost()));
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        costLabel.setForeground(new Color(46, 204, 113));
        contentPanel.add(costLabel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadImage() {
        // Try multiple possible image paths
        String photoPath = recipe.photoPath();
        
        if (photoPath != null && !photoPath.isEmpty()) {
            // Try the path as is
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                loadImageFromFile(imgFile);
                return;
            }
            
            // Try with project root path
            File projectFile = new File(System.getProperty("user.dir"), photoPath);
            if (projectFile.exists()) {
                loadImageFromFile(projectFile);
                return;
            }
            
            // Try with images directory
            File imagesFile = new File("images/recipes/" + new File(photoPath).getName());
            if (imagesFile.exists()) {
                loadImageFromFile(imagesFile);
                return;
            }
        }
        
        // Fallback: show first letter of recipe name
        setFallbackImage();
    }
    
    private void loadImageFromFile(File imgFile) {
        try {
            BufferedImage img = ImageIO.read(imgFile);
            if (img != null) {
                Image scaledImg = img.getScaledInstance(260, 140, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
                imageLabel.setText("");
            } else {
                setFallbackImage();
            }
        } catch (IOException e) {
            setFallbackImage();
        }
    }
    
    private void setFallbackImage() {
        String title = recipe.title();
        String firstLetter = (title != null && !title.isEmpty()) ? title.substring(0, 1) : "?";
        imageLabel.setIcon(null);
        imageLabel.setText("<html><div style='text-align: center; font-size: 48px; color: #cccccc;'>" + firstLetter + "</div></html>");
    }
    
    private String getDifficultyIcon(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return "🍀";
            case "medium": return "⭐";
            case "hard": return "🔥";
            default: return "❓";
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