package com.recipe.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final ArrayList<String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    
    public ValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = new ArrayList<>(errors);
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}