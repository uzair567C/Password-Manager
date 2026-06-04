package com.recipe.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.recipe.gui.ThemeManager;

public class ToastNotifier {
    
    public static final int INFO = 0;
    public static final int SUCCESS = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    
    private static final int TOAST_DURATION = 3000;
    private static final int TOAST_HEIGHT = 50;
    private static final int TOAST_WIDTH = 350;
    private static Queue<JDialog> toastQueue = new LinkedList<>();
    private static boolean isShowing = false;
    
    public static void show(JFrame parent, String message, int type) {
        show(parent, message, type, TOAST_DURATION);
    }
    
    public static void show(JFrame parent, String message, int type, int duration) {
        if (parent == null) {
            JOptionPane.showMessageDialog(null, message, getTitle(type), getMessageType(type));
            return;
        }
        
        JDialog toast = new JDialog(parent, false);
        toast.setUndecorated(true);
        toast.setSize(TOAST_WIDTH, TOAST_HEIGHT);
        toast.setBackground(new Color(0, 0, 0, 0));
        
        JPanel panel = createToastPanel(message, type);
        toast.add(panel);
        
        Point parentLocation = parent.getLocation();
        int x = parentLocation.x + (parent.getWidth() - TOAST_WIDTH) / 2;
        int y = parentLocation.y + parent.getHeight() - TOAST_HEIGHT - 30;
        toast.setLocation(x, y);
        
        toastQueue.add(toast);
        
        if (!isShowing) {
            showNextToast();
        }
        
        Timer timer = new Timer(duration, e -> fadeOutAndDispose(toast));
        timer.setRepeats(false);
        timer.start();
    }
    
    private static String getTitle(int type) {
        switch (type) {
            case SUCCESS: return "Success";
            case WARNING: return "Warning";
            case ERROR: return "Error";
            default: return "Info";
        }
    }
    
    private static int getMessageType(int type) {
        switch (type) {
            case SUCCESS: return JOptionPane.INFORMATION_MESSAGE;
            case WARNING: return JOptionPane.WARNING_MESSAGE;
            case ERROR: return JOptionPane.ERROR_MESSAGE;
            default: return JOptionPane.INFORMATION_MESSAGE;
        }
    }
    
    private static JPanel createToastPanel(String message, int type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        Color bgColor;
        Color fgColor;
        String icon;
        
        switch (type) {
            case SUCCESS:
                bgColor = ThemeManager.getInstance().getSuccessColor();
                fgColor = Color.WHITE;
                icon = "✅ ";
                break;
            case WARNING:
                bgColor = ThemeManager.getInstance().getWarningColor();
                fgColor = Color.WHITE;
                icon = "⚠️ ";
                break;
            case ERROR:
                bgColor = ThemeManager.getInstance().getErrorColor();
                fgColor = Color.WHITE;
                icon = "❌ ";
                break;
            default:
                bgColor = ThemeManager.getInstance().getPrimaryColor();
                fgColor = Color.WHITE;
                icon = "ℹ️ ";
        }
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        JLabel label = new JLabel(icon + message);
        label.setForeground(fgColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contentPanel.add(label, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static void showNextToast() {
        if (toastQueue.isEmpty()) {
            isShowing = false;
            return;
        }
        
        isShowing = true;
        JDialog toast = toastQueue.poll();
        toast.setVisible(true);
    }
    
    private static void fadeOutAndDispose(JDialog toast) {
        Timer fadeTimer = new Timer(20, new ActionListener() {
            float opacity = 1f;
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0f) {
                    toast.dispose();
                    ((Timer) e.getSource()).stop();
                    showNextToast();
                } else {
                    toast.setOpacity(opacity);
                }
            }
        });
        fadeTimer.start();
    }
}