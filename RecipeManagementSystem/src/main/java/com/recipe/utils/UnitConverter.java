package com.recipe.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    
    private static final Map<String, Map<String, Double>> CONVERSION_FACTORS = new HashMap<>();
    
    static {
        // Volume conversions
        Map<String, Double> volumeFactors = new HashMap<>();
        volumeFactors.put("cup_to_ml", 236.588);
        volumeFactors.put("ml_to_cup", 1.0 / 236.588);
        volumeFactors.put("tbsp_to_ml", 14.7868);
        volumeFactors.put("ml_to_tbsp", 1.0 / 14.7868);
        volumeFactors.put("tsp_to_ml", 4.92892);
        volumeFactors.put("ml_to_tsp", 1.0 / 4.92892);
        volumeFactors.put("tbsp_to_tsp", 3.0);
        volumeFactors.put("tsp_to_tbsp", 1.0 / 3.0);
        volumeFactors.put("cup_to_tbsp", 16.0);
        volumeFactors.put("tbsp_to_cup", 1.0 / 16.0);
        volumeFactors.put("liter_to_ml", 1000.0);
        volumeFactors.put("ml_to_liter", 0.001);
        CONVERSION_FACTORS.put("volume", volumeFactors);
        
        // Weight conversions
        Map<String, Double> weightFactors = new HashMap<>();
        weightFactors.put("g_to_kg", 0.001);
        weightFactors.put("kg_to_g", 1000.0);
        weightFactors.put("oz_to_g", 28.3495);
        weightFactors.put("g_to_oz", 1.0 / 28.3495);
        weightFactors.put("lb_to_g", 453.592);
        weightFactors.put("g_to_lb", 1.0 / 453.592);
        weightFactors.put("oz_to_lb", 0.0625);
        weightFactors.put("lb_to_oz", 16.0);
        CONVERSION_FACTORS.put("weight", weightFactors);
        
        // Temperature conversions
        Map<String, Double> tempFactors = new HashMap<>();
        CONVERSION_FACTORS.put("temperature", tempFactors);
    }
    
    public static double convertVolume(double value, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) return value;
        
        // Convert to ml first
        double inMl = value;
        switch (fromUnit.toLowerCase()) {
            case "cup": inMl = value * 236.588; break;
            case "tbsp": inMl = value * 14.7868; break;
            case "tsp": inMl = value * 4.92892; break;
            case "liter": case "l": inMl = value * 1000; break;
            case "ml": break;
            default: return value;
        }
        
        // Convert from ml to target unit
        switch (toUnit.toLowerCase()) {
            case "cup": return round(inMl / 236.588);
            case "tbsp": return round(inMl / 14.7868);
            case "tsp": return round(inMl / 4.92892);
            case "liter": case "l": return round(inMl / 1000);
            case "ml": return round(inMl);
            default: return value;
        }
    }
    
    public static double convertWeight(double value, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) return value;
        
        // Convert to grams first
        double inGrams = value;
        switch (fromUnit.toLowerCase()) {
            case "kg": inGrams = value * 1000; break;
            case "oz": inGrams = value * 28.3495; break;
            case "lb": inGrams = value * 453.592; break;
            case "g": break;
            default: return value;
        }
        
        // Convert from grams to target unit
        switch (toUnit.toLowerCase()) {
            case "kg": return round(inGrams / 1000);
            case "oz": return round(inGrams / 28.3495);
            case "lb": return round(inGrams / 453.592);
            case "g": return round(inGrams);
            default: return value;
        }
    }
    
    public static double convertTemperature(double value, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) return round(value);
        
        double celsius = value;
        if (fromUnit.equalsIgnoreCase("f") || fromUnit.equalsIgnoreCase("fahrenheit")) {
            celsius = (value - 32) * 5.0 / 9.0;
        }
        
        if (toUnit.equalsIgnoreCase("f") || toUnit.equalsIgnoreCase("fahrenheit")) {
            return round(celsius * 9.0 / 5.0 + 32);
        } else if (toUnit.equalsIgnoreCase("c") || toUnit.equalsIgnoreCase("celsius")) {
            return round(celsius);
        }
        
        return round(value);
    }
    
    public static String formatQuantity(double value, String unit) {
        if (value == Math.floor(value)) {
            return String.format("%.0f %s", value, unit);
        }
        return String.format("%.2f %s", value, unit);
    }
    
    private static double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static class ConversionResult {
        public final double value;
        public final String formatted;
        
        public ConversionResult(double value, String formatted) {
            this.value = value;
            this.formatted = formatted;
        }
    }
}