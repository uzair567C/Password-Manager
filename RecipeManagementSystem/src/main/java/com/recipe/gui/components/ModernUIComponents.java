package com.recipe.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

/**
 * Professional UI components with modern styling, gradients, and animations.
 * Provides reusable components for consistent look and feel.
 */
public class ModernUIComponents {
    
    // Modern Color Palette - Professional & Attractive
    public static class Colors {
        // Brand Colors
        public static final Color PRIMARY = new Color(41, 128, 185);      // Vibrant Blue
        public static final Color PRIMARY_LIGHT = new Color(52, 152, 219);
        public static final Color PRIMARY_DARK = new Color(30, 102, 160);
        
        // Secondary Colors  
        public static final Color SECONDARY = new Color(46, 204, 113);    // Fresh Green
        public static final Color ACCENT = new Color(230, 126, 34);       // Warm Orange
        public static final Color DANGER = new Color(231, 76, 60);        // Alert Red
        
        // Neutral Colors
        public static final Color BACKGROUND = new Color(248, 249, 250);
        public static final Color SURFACE = Color.WHITE;
        public static final Color TEXT_PRIMARY = new Color(33, 47, 61);
        public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
        public static final Color DIVIDER = new Color(224, 228, 233);
        
        // Dark Theme Colors
        public static final Color DARK_BG = new Color(20, 28, 38);
        public static final Color DARK_SURFACE = new Color(30, 42, 56);
        public static final Color DARK_TEXT = new Color(236, 240, 241);
        public static final Color DARK_TEXT_SECONDARY = new Color(189, 195, 199);
    }
    
    /**
     * Create a modern button with gradient and hover effects
     */
    public static JButton createModernButton(String text) {
        return createModernButton(text, Colors.PRIMARY, Colors.PRIMARY_DARK);
    }
    
    public static JButton createModernButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(
                        (int)(hoverColor.getRed() * 0.8),
                        (int)(hoverColor.getGreen() * 0.8),
                        (int)(hoverColor.getBlue() * 0.8)
                    ));
                } else if (getModel().isArmed() || getModel().isRollover()) {
                    g.setColor(hoverColor);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, normalColor,
                        0, getHeight(), new Color(
                            (int)(normalColor.getRed() * 0.9),
                            (int)(normalColor.getGreen() * 0.9),
                            (int)(normalColor.getBlue() * 0.9)
                        )
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    return;
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(normalColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        return button;
    }
    
    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                // Draw card background
                g2.setColor(Colors.SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                
                // Draw border
                g2.setColor(Colors.DIVIDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        return card;
    }
    
    /**
     * Create a rounded border with shadow
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final boolean shadow;
        
        public RoundedBorder(int radius, Color color, boolean shadow) {
            this.radius = radius;
            this.color = color;
            this.shadow = shadow;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            }
            
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }
    
    /**
     * Create a modern text field with rounded corners
     */
    public static JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setForeground(Colors.TEXT_PRIMARY);
        field.setCaretColor(Colors.PRIMARY);
        field.setBackground(Colors.BACKGROUND);
        field.setBorder(new RoundedBorder(6, Colors.DIVIDER, false));
        field.setPreferredSize(new Dimension(300, 40));
        field.setMargin(new Insets(8, 12, 8, 12));
        
        return field;
    }
    
    /**
     * Create a styled label with custom colors
     */
    public static JLabel createModernLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(bold ? Colors.TEXT_PRIMARY : Colors.TEXT_SECONDARY);
        return label;
    }
    
    /**
     * Create a modern progress indicator
     */
    public static JProgressBar createModernProgressBar() {
        JProgressBar bar = new JProgressBar();
        bar.setForeground(Colors.PRIMARY);
        bar.setBackground(Colors.BACKGROUND);
        bar.setPreferredSize(new Dimension(300, 8));
        return bar;
    }
    
    /**
     * Create a styled combo box
     */
    public static <T> JComboBox<T> createModernComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setForeground(Colors.TEXT_PRIMARY);
        combo.setBackground(Colors.SURFACE);
        combo.setPreferredSize(new Dimension(200, 38));
        return combo;
    }
    
    /**
     * Add smooth hover effect to a component
     */
    public static void addHoverEffect(JComponent component, Color normalBg, Color hoverBg) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBackground(hoverBg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(normalBg);
            }
        });
    }
}
