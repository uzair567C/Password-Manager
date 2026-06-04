package com.recipe.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import com.recipe.models.GroceryItem;
import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;

public class PDFExporter {
    
    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float FOOTER_SPACE = 40;
    private static final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDFont FONT_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_ITALIC = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
    private static final PDFont FONT_SMALL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    
    public File exportRecipeToPDF(Recipe recipe, String outputPath) throws IOException {
        if (recipe == null) {
            throw new IOException("Recipe cannot be null");
        }
        
        File outputDir = new File(outputPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Could not create output directory: " + outputPath);
        }
        
        String safeFileName = recipe.title() != null && !recipe.title().isBlank()
                ? recipe.title().replaceAll("[^a-zA-Z0-9]", "_")
                : "recipe";
        String fileName = outputDir.getAbsolutePath() + File.separator + safeFileName + ".pdf";
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = addPage(document);
            PDPageContentStream contentStream = openPageStream(document, page);
            
            try {
                float y = page.getMediaBox().getHeight() - MARGIN;
                writeText(contentStream, FONT_BOLD, 20, MARGIN, y, truncateText(recipe.title(), 50));
                y -= 35;
                
                String metadata = String.format("Prep: %d min | Cook: %d min | Servings: %d | Difficulty: %s",
                        recipe.prepTime(), recipe.cookTime(), recipe.servings(),
                        recipe.difficulty() != null && !recipe.difficulty().isBlank() ? recipe.difficulty() : "Medium");
                writeText(contentStream, FONT_NORMAL, 10, MARGIN, y, metadata);
                y -= 25;
                
                drawSeparator(contentStream, y);
                y -= 20;
                
                y = printSectionHeading(contentStream, "Ingredients", y);
                
                List<RecipeIngredient> ingredients = recipe.ingredients();
                if (ingredients != null && !ingredients.isEmpty()) {
                    contentStream.setFont(FONT_NORMAL, 11);
                    for (int i = 0; i < ingredients.size(); i++) {
                        if (y < MARGIN + 80) {
                            contentStream.close();
                            page = addPage(document);
                            contentStream = openPageStream(document, page);
                            y = page.getMediaBox().getHeight() - MARGIN;
                            y = printSectionHeading(contentStream, "Ingredients (continued)", y);
                            contentStream.setFont(FONT_NORMAL, 11);
                        }
                        RecipeIngredient ri = ingredients.get(i);
                        String ingredientName = ri.ingredient() != null ? ri.ingredient().name() : "Unknown";
                        String quantity = ri.getFormattedQuantity() != null ? ri.getFormattedQuantity() : "0";
                        String line = truncateText(String.format("• %s: %s", ingredientName, quantity), 60);
                        writeText(contentStream, FONT_NORMAL, 11, MARGIN + 10, y, line);
                        y -= 15;
                    }
                    y -= 15;
                } else {
                    writeText(contentStream, FONT_NORMAL, 11, MARGIN + 10, y, "No ingredients listed");
                    y -= 20;
                }
                
                y = printSectionHeading(contentStream, "Instructions", y);
                
                contentStream.setFont(FONT_NORMAL, 11);
                String[] instructions = getInstructionsFromRecipe(recipe);
                for (String instruction : instructions) {
                    if (y < MARGIN + 60) {
                        contentStream.close();
                        page = addPage(document);
                        contentStream = openPageStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }
                    writeText(contentStream, FONT_NORMAL, 11, MARGIN + 10, y, truncateText(instruction, 65));
                    y -= 15;
                }
                
                Nutrition nutrition = recipe.nutrition();
                if (nutrition != null && nutrition.calories() > 0) {
                    y -= 15;
                    if (y < MARGIN + 80) {
                        contentStream.close();
                        page = addPage(document);
                        contentStream = openPageStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }
                    writeText(contentStream, FONT_BOLD, 14, MARGIN, y, "Nutrition Facts (per serving)");
                    y -= 20;
                    int servings = recipe.servings() > 0 ? recipe.servings() : 1;
                    writeText(contentStream, FONT_NORMAL, 11, MARGIN + 10, y,
                            String.format("Calories: %d kcal", nutrition.getCaloriesPerServing(servings)));
                    y -= 15;
                    writeText(contentStream, FONT_NORMAL, 11, MARGIN + 10, y,
                            String.format("Protein: %.1f g | Carbs: %.1f g | Fat: %.1f g",
                                    nutrition.getProteinPerServing(servings),
                                    nutrition.getCarbsPerServing(servings),
                                    nutrition.getFatPerServing(servings)));
                    y -= 20;
                }
                
                if (y < FOOTER_SPACE) {
                    contentStream.close();
                    page = addPage(document);
                    contentStream = openPageStream(document, page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }
                
                writeText(contentStream, FONT_SMALL, 8, MARGIN, MARGIN,
                        "Generated by Recipe Management System on " +
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            document.save(fileName);
        }
        return new File(fileName);
    }
    
    public File exportGroceryListToPDF(List<GroceryItem> groceryList, String outputPath) throws IOException {
        if (groceryList == null || groceryList.isEmpty()) {
            throw new IOException("Grocery list cannot be empty");
        }
        
        File outputDir = new File(outputPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Could not create output directory: " + outputPath);
        }
        
        String fileName = outputDir.getAbsolutePath() + File.separator + "Grocery_List_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = addPage(document);
            PDPageContentStream contentStream = openPageStream(document, page);
            
            try {
                float y = page.getMediaBox().getHeight() - MARGIN;
                writeText(contentStream, FONT_BOLD, 18, MARGIN, y, "Grocery Shopping List");
                y -= 35;
                writeText(contentStream, FONT_NORMAL, 10, MARGIN, y,
                        "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                y -= 25;
                drawSeparator(contentStream, y);
                y -= 20;
                
                contentStream.setFont(FONT_NORMAL, 12);
                for (int i = 0; i < groceryList.size(); i++) {
                    if (y < MARGIN + 50) {
                        contentStream.close();
                        page = addPage(document);
                        contentStream = openPageStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }
                    GroceryItem item = groceryList.get(i);
                    String checkbox = item.isPurchased() ? "[✓] " : "[ ] ";
                    String formattedQty = item.getFormattedQuantity() != null ? item.getFormattedQuantity() : "0";
                    String line = truncateText(checkbox + item.ingredientName() + " - " + formattedQty, 70);
                    writeText(contentStream, FONT_NORMAL, 12, MARGIN + 10, y, line);
                    y -= 18;
                }
                
                if (y > MARGIN) {
                    y -= 15;
                    writeText(contentStream, FONT_SMALL, 8, MARGIN, y, "Total items: " + groceryList.size());
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            document.save(fileName);
        }
        return new File(fileName);
    }
    
    private PDPage addPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        return page;
    }
    
    private PDPageContentStream openPageStream(PDDocument document, PDPage page) throws IOException {
        return new PDPageContentStream(document, page);
    }
    
    private void writeText(PDPageContentStream contentStream, PDFont font, float fontSize,
                           float x, float y, String text) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
    
    private void drawSeparator(PDPageContentStream contentStream, float y) throws IOException {
        contentStream.setStrokingColor(0.5f, 0.5f, 0.5f);
        contentStream.moveTo(MARGIN, y);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, y);
        contentStream.stroke();
    }
    
    private float printSectionHeading(PDPageContentStream contentStream, String heading, float y) throws IOException {
        writeText(contentStream, FONT_BOLD, 14, MARGIN, y, heading);
        return y - 20;
    }
    
    // Helper method to truncate text that's too long
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    // Helper method to get instructions from recipe or provide defaults
    private String[] getInstructionsFromRecipe(Recipe recipe) {
        return new String[]{
            "1. Prepare all ingredients according to the list above.",
            "2. Follow your preferred cooking method for this recipe.",
            "3. Adjust seasoning to taste.",
            "4. Serve hot and enjoy!"
        };
    }
}