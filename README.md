# Expense Tracker Application

A simple and intuitive expense tracking application built with Java backend and vanilla HTML/CSS/JavaScript frontend.

# Features

- Add expenses with category, amount, date, and description
- View all expenses in a sortable table
- Calculate total expenses
- Group expenses by category
- Visualize spending trends with interactive charts
- Identify highest and lowest spending categories
- Delete expenses
- Persistent storage using JSON file
- Responsive design for mobile and desktop

# Architecture

Backend (Java)

Model: Expense.java - POJO representing expense data
Service:

ExpenseManager.java - Business logic and analytics
FileStorage.java - JSON file persistence


Server: ExpenseServer.java - HTTP server with REST API

 Frontend

HTML: Structure and layout
CSS: Modern, gradient-based styling
JavaScript: API communication and dynamic UI updates
Chart.js: Data visualization

# Project Structure

expense-tracker/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── expensetracker/
│                   ├── model/
│                   │   └── Expense.java
│                   ├── service/
│                   │   ├── ExpenseManager.java
│                   │   └── FileStorage.java
│                   └── server/
│                       └── ExpenseServer.java
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── app.js
├── data/
│   └── expenses.json (auto-generated)
├── test/
│   └── seed-data.json
└── README.md

# Prerequisites

Java JDK 11 or higher
Web browser (Chrome, Firefox, Safari, Edge)

Installation & Running

Clone the repository

bashgit clone <your-repo-url>
cd expense-tracker

Compile Java files

bash# Create bin directory
mkdir -p bin

# Compile all Java source files
javac -d bin src/main/java/com/expensetracker/model/*.java
javac -d bin -cp bin src/main/java/com/expensetracker/service/*.java
javac -d bin -cp bin src/main/java/com/expensetracker/server/*.java

# Run the server

bashjava -cp bin com.expensetracker.server.ExpenseServer

# Access the application
Open your browser and navigate to:

http://localhost:8080

Alternative: Using an IDE
IntelliJ IDEA / Eclipse:

 Import the project
Right-click on ExpenseServer.java
Select "Run 'ExpenseServer.main()'"
Open http://localhost:8080 in your browser

# Testing and Verification
Manual Testing Steps

Add Expense Test

Fill in all form fields (category, amount, date, description)
Click "Add Expense"
   -Verify expense appears in the table
   -Check that statistics update


Total Calculation Test

Add multiple expenses
  -Verify total matches sum of all amounts


Category Analysis Test

Add expenses in different categories
   -Check "By Category" doughnut chart
   -Verify highest/lowest categories are correct


Trend Visualization Test

Add expenses on different dates
  -Check trend line chart displays correctly
  -Verify dates are in chronological order


Delete Functionality Test

Click delete button on an expense
Confirm deletion
   -Verify expense is removed
   -Check statistics recalculate


Persistence Test

Add several expenses
Stop the server (Ctrl+C)
Restart the server
Reload the browser
  -Verify all expenses are still present



Loading Test Data

Option 1: Manual Entry

Use the web interface to add expenses manually

Option 2: Load Seed Data

Copy content from test/seed-data.json
Paste into data/expenses.json
Restart the server
Refresh the browser

# Seed Data Sample:
json[
  {
    "id": 1,
    "category": "Food",
    "amount": 50.00,
    "date": "2025-01-01",
    "description": "Grocery shopping"
  },
  {
    "id": 2,
    "category": "Transport",
    "amount": 30.00,
    "date": "2025-01-02",
    "description": "Gas refill"
  },
  {
    "id": 3,
    "category": "Entertainment",
    "amount": 75.00,
    "date": "2025-01-03",
    "description": "Movie tickets and dinner"
  },
  {
    "id": 4,
    "category": "Bills",
    "amount": 120.00,
    "date": "2025-01-04",
    "description": "Electricity bill"
  },
  {
    "id": 5,
    "category": "Shopping",
    "amount": 200.00,
    "date": "2025-01-05",
    "description": "New clothes"
  }
]

# API Endpoints

GET /api/expenses
Returns all expenses
Response:
json[
  {
    "id": 1,
    "category": "Food",
    "amount": 50.00,
    "date": "2025-01-01",
    "description": "Grocery shopping"
  }
]
POST /api/expenses
Add a new expense
Request Body:
json{
  "category": "Food",
  "amount": 50.00,
  "date": "2025-01-01",
  "description": "Grocery shopping"
}
DELETE /api/expenses/{id}
Delete an expense by ID
GET /api/analytics
Get analytics data
Response:
json{
  "total": 275.00,
  "byCategory": {
    "Food": 50.00,
    "Transport": 30.00,
    "Entertainment": 75.00
  },
  "highest": {
    "category": "Bills",
    "amount": 120.00
  },
  "lowest": {
    "category": "Transport",
    "amount": 30.00
  },
  "trend": [
    {"date": "2025-01-01", "amount": 50.00},
    {"date": "2025-01-02", "amount": 30.00}
  ]
}
# Design Approach

Backend Design

Separation of Concerns: Model, Service, and Server layers
RESTful API: Standard HTTP methods (GET, POST, DELETE)
File-based Storage: Simple JSON persistence without database
Pure Java: No external dependencies, only standard library

Frontend Design

Single Page Application: All features in one page
Responsive Layout: Works on mobile and desktop
Modern UI: Gradient backgrounds, smooth animations
Real-time Updates: Instant feedback on all actions
Visual Analytics: Charts for better data understanding

Key Design Decisions

No Framework: Pure Java and vanilla JavaScript as requested
JSON Storage: Simple, human-readable, no database setup
Built-in HTTP Server: Uses com.sun.net.httpserver (Java SE)
Client-side Rendering: JavaScript updates DOM dynamically
Chart.js: CDN-loaded library for visualizations

# Troubleshooting

Server won't start

Check if port 8080 is already in use
Try changing PORT constant in ExpenseServer.java

Frontend not loading

Ensure frontend/ directory is in the same location as where you run the server
Check browser console for errors

Data not persisting

Check if data/ directory has write permissions
Verify expenses.json file is being created

Compilation errors

Ensure JDK 11 or higher is installed
Verify all source files are in correct directory structure

# Future Enhancements

 Edit expense functionality
 Filter by date range
 Export to CSV/PDF
 Budget limits and alerts
 Multi-user support with authentication
 Mobile app version
 Receipt image upload

