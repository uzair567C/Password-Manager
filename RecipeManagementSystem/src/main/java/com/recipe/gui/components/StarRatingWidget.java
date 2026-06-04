package com.recipe.gui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StarRatingWidget extends JPanel {
    
    private final JLabel[] stars;
    private int currentRating = 0;
    private Consumer<Integer> onRatingChanged;
    
    public StarRatingWidget() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setOpaque(false);
        
        stars = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            stars[i] = new JLabel("☆");
            stars[i].setFont(new Font("Segoe UI", Font.PLAIN, 20));
            stars[i].setForeground(new Color(241, 196, 15));
            stars[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            stars[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setRating(starIndex);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    highlightStars(starIndex);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    highlightStars(currentRating);
                }
            });
            add(stars[i]);
        }
    }
    
    public void setRating(int rating) {
        this.currentRating = Math.max(0, Math.min(5, rating));
        highlightStars(currentRating);
        if (onRatingChanged != null) {
            onRatingChanged.accept(currentRating);
        }
    }
    
    public int getRating() {
        return currentRating;
    }
    
    public void setEnabled(boolean enabled) {
        for (JLabel star : stars) {
            star.setEnabled(enabled);
            star.setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    public void setOnRatingChanged(Consumer<Integer> callback) {
        this.onRatingChanged = callback;
    }
    
    private void highlightStars(int count) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < count ? "★" : "☆");
        }
    }
}