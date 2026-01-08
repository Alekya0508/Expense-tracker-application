package main.java.com.expensetracker.service;

import main.java.com.expensetracker.model.Expense;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that handles all expense-related business logic
 */
public class ExpenseManager {
    private List<Expense> expenses;
    private FileStorage fileStorage;
    private long nextId;

    public ExpenseManager() {
        this.fileStorage = new FileStorage();
        this.expenses = fileStorage.loadExpenses();
        
        // Set next ID based on existing expenses
        this.nextId = expenses.stream()
                .mapToLong(Expense::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Add a new expense
     */
    public Expense addExpense(String category, double amount, String date, String description) {
        Expense expense = new Expense(nextId++, category, amount, date, description);
        expenses.add(expense);
        saveToFile();
        return expense;
    }

    /**
     * Get all expenses
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Delete an expense by ID
     */
    public boolean deleteExpense(long id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Calculate total expense
     */
    public double getTotalExpense() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Get total expense by category
     */
    public Map<String, Double> getExpenseByCategory() {
        Map<String, Double> categoryTotals = new HashMap<>();
        
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        
        return categoryTotals;
    }

    /**
     * Get expense trend by date
     */
    public Map<String, Double> getExpenseTrend() {
        Map<String, Double> dateTotals = new TreeMap<>();
        
        for (Expense expense : expenses) {
            dateTotals.merge(expense.getDate(), expense.getAmount(), Double::sum);
        }
        
        return dateTotals;
    }

    /**
     * Get highest spending category
     */
    public Map.Entry<String, Double> getHighestSpendCategory() {
        Map<String, Double> categoryTotals = getExpenseByCategory();
        
        return categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    /**
     * Get lowest spending category
     */
    public Map.Entry<String, Double> getLowestSpendCategory() {
        Map<String, Double> categoryTotals = getExpenseByCategory();
        
        return categoryTotals.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);
    }

    /**
     * Get analytics data
     */
    public String getAnalyticsJson() {
        StringBuilder json = new StringBuilder("{");
        
        // Total expense
        json.append("\"total\":").append(String.format("%.2f", getTotalExpense())).append(",");
        
        // By category
        json.append("\"byCategory\":{");
        Map<String, Double> byCategory = getExpenseByCategory();
        int count = 0;
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            if (count > 0) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":")
                .append(String.format("%.2f", entry.getValue()));
            count++;
        }
        json.append("},");
        
        // Highest and Lowest
        Map.Entry<String, Double> highest = getHighestSpendCategory();
        Map.Entry<String, Double> lowest = getLowestSpendCategory();
        
        if (highest != null) {
            json.append("\"highest\":{\"category\":\"").append(highest.getKey())
                .append("\",\"amount\":").append(String.format("%.2f", highest.getValue())).append("},");
        } else {
            json.append("\"highest\":null,");
        }
        
        if (lowest != null) {
            json.append("\"lowest\":{\"category\":\"").append(lowest.getKey())
                .append("\",\"amount\":").append(String.format("%.2f", lowest.getValue())).append("},");
        } else {
            json.append("\"lowest\":null,");
        }
        
        // Trend
        json.append("\"trend\":[");
        Map<String, Double> trend = getExpenseTrend();
        count = 0;
        for (Map.Entry<String, Double> entry : trend.entrySet()) {
            if (count > 0) json.append(",");
            json.append("{\"date\":\"").append(entry.getKey())
                .append("\",\"amount\":").append(String.format("%.2f", entry.getValue())).append("}");
            count++;
        }
        json.append("]");
        
        json.append("}");
        return json.toString();
    }

    /**
     * Save expenses to file
     */
    private void saveToFile() {
        try {
            fileStorage.saveExpenses(expenses);
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }

    /**
     * Convert expenses list to JSON
     */
    public String expensesToJson() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < expenses.size(); i++) {
            json.append(expenses.get(i).toJson());
            if (i < expenses.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}