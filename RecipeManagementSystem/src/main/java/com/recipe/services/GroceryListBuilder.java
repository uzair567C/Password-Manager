package com.recipe.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.recipe.models.GroceryItem;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;

public class GroceryListBuilder {
    
    // Helper class to track accumulating quantities
    private static class QuantityAccumulator {
        String ingredientName;
        String unit;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        boolean isPurchased = false;
        
        QuantityAccumulator(String ingredientName, String unit) {
            this.ingredientName = ingredientName;
            this.unit = unit;
        }
        
        void addQuantity(BigDecimal quantity) {
            this.totalQuantity = this.totalQuantity.add(quantity);
        }
    }
    
    public List<GroceryItem> buildGroceryList(List<Recipe> recipes) {
        if (recipes == null || recipes.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<Integer, QuantityAccumulator> accumulatorMap = new HashMap<>();
        
        for (Recipe recipe : recipes) {
            if (recipe.ingredients() != null) {
                for (RecipeIngredient ri : recipe.ingredients()) {
                    int ingredientId = ri.ingredient().id();
                    String ingredientName = ri.ingredient().name();
                    String unit = ri.unit();
                    
                    QuantityAccumulator accumulator = accumulatorMap.get(ingredientId);
                    if (accumulator == null) {
                        accumulator = new QuantityAccumulator(ingredientName, unit);
                        accumulatorMap.put(ingredientId, accumulator);
                    }
                    
                    accumulator.addQuantity(ri.quantity());
                }
            }
        }
        
        // Convert accumulators to GroceryItem objects
        return accumulatorMap.entrySet().stream()
            .map(entry -> GroceryItem.builder()
                .ingredientId(entry.getKey())
                .ingredientName(entry.getValue().ingredientName)
                .totalQuantity(entry.getValue().totalQuantity)
                .unit(entry.getValue().unit)
                .isPurchased(entry.getValue().isPurchased)
                .build())
            .sorted(Comparator.comparing(GroceryItem::ingredientName))
            .collect(Collectors.toList());
    }
    
    public List<GroceryItem> mergeGroceryLists(List<GroceryItem> list1, List<GroceryItem> list2) {
        Map<Integer, QuantityAccumulator> mergedMap = new HashMap<>();
        
        // Add first list
        for (GroceryItem item : list1) {
            QuantityAccumulator acc = new QuantityAccumulator(item.ingredientName(), item.unit());
            acc.totalQuantity = item.totalQuantity();
            acc.isPurchased = item.isPurchased();
            mergedMap.put(item.ingredientId(), acc);
        }
        
        // Merge second list
        for (GroceryItem item : list2) {
            QuantityAccumulator existing = mergedMap.get(item.ingredientId());
            if (existing != null) {
                existing.addQuantity(item.totalQuantity());
                // Keep purchased status if either is purchased
                existing.isPurchased = existing.isPurchased || item.isPurchased();
            } else {
                QuantityAccumulator acc = new QuantityAccumulator(item.ingredientName(), item.unit());
                acc.totalQuantity = item.totalQuantity();
                acc.isPurchased = item.isPurchased();
                mergedMap.put(item.ingredientId(), acc);
            }
        }
        
        // Convert to GroceryItem objects
        return mergedMap.entrySet().stream()
            .map(entry -> GroceryItem.builder()
                .ingredientId(entry.getKey())
                .ingredientName(entry.getValue().ingredientName)
                .totalQuantity(entry.getValue().totalQuantity)
                .unit(entry.getValue().unit)
                .isPurchased(entry.getValue().isPurchased)
                .build())
            .sorted(Comparator.comparing(GroceryItem::ingredientName))
            .collect(Collectors.toList());
    }
    
    public BigDecimal calculateEstimatedCost(List<GroceryItem> groceryList) {
        // This would need ingredient prices from database
        // For now, return a placeholder or calculate from available data
        return groceryList.stream()
            .map(item -> {
                // TODO: Fetch price per unit from database
                // For now, return a default small amount per item
                return BigDecimal.valueOf(0.50); // Placeholder $0.50 per item
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Alternative method that takes recipe list directly and calculates cost
    public BigDecimal calculateEstimatedCostForRecipes(List<Recipe> recipes) {
        if (recipes == null || recipes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return recipes.stream()
            .map(recipe -> BigDecimal.valueOf(recipe.cost()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}