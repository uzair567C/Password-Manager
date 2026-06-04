package com.recipe.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class SearchBar extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final JTextField searchField;
    private final JButton clearButton;
    private Consumer<String> searchListener;
    private Timer debounceTimer;
    
    public SearchBar() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(250, 35));
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(new Color(150, 150, 150));
        add(searchIcon, BorderLayout.WEST);
        
        searchField = new JTextField();
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Search your recipes...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (debounceTimer != null && debounceTimer.isRunning()) {
                    debounceTimer.stop();
                }
                debounceTimer = new Timer(300, evt -> {
                    if (searchListener != null) {
                        searchListener.accept(searchField.getText());
                    }
                });
                debounceTimer.setRepeats(false);
                debounceTimer.start();
            }
        });
        add(searchField, BorderLayout.CENTER);
        
        clearButton = new JButton("✖");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            if (searchListener != null) {
                searchListener.accept("");
            }
        });
        add(clearButton, BorderLayout.EAST);
    }
    
    public void addSearchListener(Consumer<String> listener) {
        this.searchListener = listener;
    }
    
    public String getText() {
        return searchField.getText();
    }
    
    public void setText(String text) {
        searchField.setText(text);
    }
    
    public void clear() {
        searchField.setText("");
        if (searchListener != null) {
            searchListener.accept("");
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        searchField.setEnabled(enabled);
        clearButton.setEnabled(enabled);
    }
}