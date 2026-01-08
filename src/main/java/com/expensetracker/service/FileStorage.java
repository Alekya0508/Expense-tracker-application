package main.java.com.expensetracker.service;

import main.java.com.expensetracker.model.Expense;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles file-based storage of expenses in JSON format
 */
public class FileStorage {
    private static final String DATA_FILE = "data/expenses.json";

    public FileStorage() {
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    /**
     * Save expenses to file
     */
    public void saveExpenses(List<Expense> expenses) throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < expenses.size(); i++) {
            json.append("  ").append(expenses.get(i).toJson());
            if (i < expenses.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write(json.toString());
        }
    }

    /**
     * Load expenses from file
     */
    public List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return expenses;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String json = content.toString().trim();
            if (json.isEmpty() || json.equals("[]")) {
                return expenses;
            }

            // Remove outer brackets
            json = json.substring(1, json.length() - 1).trim();

            // Split by },{ to get individual expense objects
            String[] expenseStrings = json.split("\\},\\s*\\{");

            for (String expenseStr : expenseStrings) {
                expenseStr = expenseStr.trim();
                if (!expenseStr.startsWith("{")) {
                    expenseStr = "{" + expenseStr;
                }
                if (!expenseStr.endsWith("}")) {
                    expenseStr = expenseStr + "}";
                }

                Expense expense = parseExpense(expenseStr);
                if (expense != null) {
                    expenses.add(expense);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }

        return expenses;
    }

    /**
     * Parse JSON string to Expense object
     */
    private Expense parseExpense(String json) {
        try {
            Expense expense = new Expense();

            // Extract id
            String idStr = extractValue(json, "id");
            if (idStr != null) {
                expense.setId(Long.parseLong(idStr));
            }

            // Extract category
            expense.setCategory(extractValue(json, "category"));

            // Extract amount
            String amountStr = extractValue(json, "amount");
            if (amountStr != null) {
                expense.setAmount(Double.parseDouble(amountStr));
            }

            // Extract date
            expense.setDate(extractValue(json, "date"));

            // Extract description
            expense.setDescription(extractValue(json, "description"));

            return expense;
        } catch (Exception e) {
            System.err.println("Error parsing expense: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract value from JSON string for a given key
     */
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchKey.length();
        
        // Skip whitespace
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }

        if (startIndex >= json.length()) {
            return null;
        }

        // Check if value is a string (starts with quote)
        if (json.charAt(startIndex) == '"') {
            startIndex++; // Skip opening quote
            int endIndex = json.indexOf('"', startIndex);
            if (endIndex == -1) {
                return null;
            }
            return json.substring(startIndex, endIndex);
        } else {
            // Numeric value
            int endIndex = startIndex;
            while (endIndex < json.length() && 
                   (Character.isDigit(json.charAt(endIndex)) || 
                    json.charAt(endIndex) == '.' || 
                    json.charAt(endIndex) == '-')) {
                endIndex++;
            }
            return json.substring(startIndex, endIndex);
        }
    }
}