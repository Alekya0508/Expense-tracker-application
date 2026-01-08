package main.java.com.expensetracker.model;

import java.time.LocalDate;

/**
 * Expense model class representing a single expense entry
 */
public class Expense {
    private long id;
    private String category;
    private double amount;
    private String date; // Format: YYYY-MM-DD
    private String description;

    // Constructors
    public Expense() {
    }

    public Expense(long id, String category, double amount, String date, String description) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Convert to JSON string manually
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"category\":\"%s\",\"amount\":%.2f,\"date\":\"%s\",\"description\":\"%s\"}",
            id, category, amount, date, description != null ? description : ""
        );
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
