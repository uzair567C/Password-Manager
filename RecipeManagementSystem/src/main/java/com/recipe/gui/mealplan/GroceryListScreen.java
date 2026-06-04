package com.recipe.gui.mealplan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.recipe.gui.components.ToastNotifier;
import com.recipe.models.GroceryItem;
import com.recipe.services.MealPlanService;
import com.recipe.utils.PDFExporter;

public class GroceryListScreen extends JPanel {
    
    private final MealPlanService mealPlanService;
    private final PDFExporter pdfExporter;
    private final LocalDate weekStart;
    private DefaultListModel<GroceryItem> listModel;
    private JList<GroceryItem> groceryList;
    private JLabel totalItemsLabel;
    private JButton exportPDFButton;
    private JButton refreshButton;
    
    public GroceryListScreen(LocalDate weekStart) {
        this.mealPlanService = new MealPlanService();
        this.pdfExporter = new PDFExporter();
        this.weekStart = weekStart;
        initComponents();
        loadGroceryList();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Grocery List
        listModel = new DefaultListModel<>();
        groceryList = new JList<>(listModel);
        groceryList.setCellRenderer(new GroceryItemRenderer());
        groceryList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        groceryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(groceryList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("🛒 Grocery Shopping List");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(46, 134, 222));
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(Color.WHITE);
        
        totalItemsLabel = new JLabel("0 items");
        totalItemsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalItemsLabel.setForeground(new Color(100, 100, 100));
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.addActionListener(e -> loadGroceryList());
        
        statsPanel.add(totalItemsLabel);
        statsPanel.add(refreshButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        exportPDFButton = new JButton("📄 Export as PDF");
        exportPDFButton.setBackground(new Color(52, 152, 219));
        exportPDFButton.setForeground(Color.WHITE);
        exportPDFButton.setFocusPainted(false);
        exportPDFButton.addActionListener(e -> exportToPDF());
        
        JButton printButton = new JButton("🖨️ Print");
        printButton.setBackground(new Color(155, 89, 182));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.addActionListener(e -> printList());
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            if (dialog != null) dialog.dispose();
        });
        
        panel.add(exportPDFButton);
        panel.add(printButton);
        panel.add(closeButton);
        
        return panel;
    }
    
    private void loadGroceryList() {
        listModel.clear();
        
        SwingWorker<List<GroceryItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<GroceryItem> doInBackground() throws Exception {
                return mealPlanService.generateGroceryList(weekStart);
            }
            
            @Override
            protected void done() {
                try {
                    List<GroceryItem> items = get();
                    for (GroceryItem item : items) {
                        listModel.addElement(item);
                    }
                    totalItemsLabel.setText(items.size() + " items");
                    
                    if (items.isEmpty()) {
                        JLabel emptyLabel = new JLabel("No meals planned for this week. Add some recipes to your meal planner first!");
                        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                        emptyLabel.setForeground(new Color(150, 150, 150));
                        removeAll();
                        add(emptyLabel);
                    }
                    
                } catch (Exception e) {
                    ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(GroceryListScreen.this), 
                        "Failed to load grocery list: " + e.getMessage(), ToastNotifier.ERROR);
                }
            }
        };
        worker.execute();
    }
    
    private void exportToPDF() {
        List<GroceryItem> items = getCurrentList();
        if (items.isEmpty()) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "No items to export", ToastNotifier.WARNING);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Grocery_List_" + weekStart + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getParent();
                File pdfFile = pdfExporter.exportGroceryListToPDF(items, path);
                ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "PDF exported: " + pdfFile.getName(), ToastNotifier.SUCCESS);
            } catch (Exception e) {
                ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Export failed: " + e.getMessage(), ToastNotifier.ERROR);
            }
        }
    }
    
    private void printList() {
        List<GroceryItem> items = getCurrentList();
        if (items.isEmpty()) {
            ToastNotifier.show((JFrame) SwingUtilities.getWindowAncestor(this), 
                "No items to print", ToastNotifier.WARNING);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Grocery Shopping List\n");
        sb.append("=====================\n\n");
        
        for (GroceryItem item : items) {
            sb.append("[ ] ").append(item.ingredientName())
              .append(" - ").append(item.getFormattedQuantity()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
            "Print Preview", JOptionPane.PLAIN_MESSAGE);
    }
    
    private List<GroceryItem> getCurrentList() {
        List<GroceryItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            items.add(listModel.get(i));
        }
        return items;
    }
    
    private static class GroceryItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean isSelected, boolean cellHasFocus) {
            if (value instanceof GroceryItem item) {
                String text = (item.isPurchased() ? "✓ " : "○ ") + 
                    item.ingredientName() + " - " + item.getFormattedQuantity();
                Component c = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                
                if (item.isPurchased()) {
                    c.setForeground(new Color(150, 150, 150));
                    c.setFont(c.getFont().deriveFont(Font.ITALIC));
                } else {
                    c.setForeground(Color.BLACK);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}