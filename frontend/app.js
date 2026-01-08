// API Base URL
const API_BASE = 'http://localhost:8080/api';

// Chart instances
let trendChart = null;
let categoryChart = null;

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    // Set today's date as default
    document.getElementById('date').valueAsDate = new Date();
    
    // Load initial data
    loadExpenses();
    loadAnalytics();
    
    // Setup form submission
    document.getElementById('expenseForm').addEventListener('submit', handleAddExpense);
});

// Load all expenses
async function loadExpenses() {
    try {
        const response = await fetch(`${API_BASE}/expenses`);
        const expenses = await response.json();
        
        renderExpensesTable(expenses);
    } catch (error) {
        console.error('Error loading expenses:', error);
        showError('Failed to load expenses');
    }
}

// Load analytics data
async function loadAnalytics() {
    try {
        const response = await fetch(`${API_BASE}/analytics`);
        const analytics = await response.json();
        
        updateStatistics(analytics);
        updateCharts(analytics);
    } catch (error) {
        console.error('Error loading analytics:', error);
    }
}

// Handle add expense form submission
async function handleAddExpense(e) {
    e.preventDefault();
    
    const expense = {
        category: document.getElementById('category').value,
        amount: parseFloat(document.getElementById('amount').value),
        date: document.getElementById('date').value,
        description: document.getElementById('description').value
    };
    
    try {
        const response = await fetch(`${API_BASE}/expenses`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(expense)
        });
        
        if (response.ok) {
            // Reset form
            document.getElementById('expenseForm').reset();
            document.getElementById('date').valueAsDate = new Date();
            
            // Reload data
            loadExpenses();
            loadAnalytics();
            
            showSuccess('Expense added successfully!');
        } else {
            showError('Failed to add expense');
        }
    } catch (error) {
        console.error('Error adding expense:', error);
        showError('Failed to add expense');
    }
}

// Delete expense
async function deleteExpense(id) {
    if (!confirm('Are you sure you want to delete this expense?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/expenses/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            loadExpenses();
            loadAnalytics();
            showSuccess('Expense deleted successfully!');
        } else {
            showError('Failed to delete expense');
        }
    } catch (error) {
        console.error('Error deleting expense:', error);
        showError('Failed to delete expense');
    }
}

// Render expenses table
function renderExpensesTable(expenses) {
    const tbody = document.getElementById('expensesTableBody');
    
    if (expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state"><p>No expenses yet. Add your first expense above!</p></td></tr>';
        return;
    }
    
    // Sort by date (newest first)
    expenses.sort((a, b) => new Date(b.date) - new Date(a.date));
    
    tbody.innerHTML = expenses.map(expense => `
        <tr>
            <td>${formatDate(expense.date)}</td>
            <td><span class="category-badge">${expense.category}</span></td>
            <td><strong>$${expense.amount.toFixed(2)}</strong></td>
            <td>${expense.description || '-'}</td>
            <td>
                <button class="btn-delete" onclick="deleteExpense(${expense.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

// Update statistics
function updateStatistics(analytics) {
    document.getElementById('totalExpense').textContent = `$${analytics.total.toFixed(2)}`;
    
    if (analytics.highest) {
        document.getElementById('highestCategory').textContent = analytics.highest.category;
        document.getElementById('highestAmount').textContent = `$${analytics.highest.amount.toFixed(2)}`;
    } else {
        document.getElementById('highestCategory').textContent = '-';
        document.getElementById('highestAmount').textContent = '$0.00';
    }
    
    if (analytics.lowest) {
        document.getElementById('lowestCategory').textContent = analytics.lowest.category;
        document.getElementById('lowestAmount').textContent = `$${analytics.lowest.amount.toFixed(2)}`;
    } else {
        document.getElementById('lowestCategory').textContent = '-';
        document.getElementById('lowestAmount').textContent = '$0.00';
    }
}

// Update charts
function updateCharts(analytics) {
    updateTrendChart(analytics.trend);
    updateCategoryChart(analytics.byCategory);
}

// Update trend chart
function updateTrendChart(trendData) {
    const ctx = document.getElementById('trendChart').getContext('2d');
    
    if (trendChart) {
        trendChart.destroy();
    }
    
    const labels = trendData.map(item => formatDate(item.date));
    const data = trendData.map(item => item.amount);
    
    trendChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Daily Expenses',
                data: data,
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(2);
                        }
                    }
                }
            }
        }
    });
}

// Update category chart
function updateCategoryChart(categoryData) {
    const ctx = document.getElementById('categoryChart').getContext('2d');
    
    if (categoryChart) {
        categoryChart.destroy();
    }
    
    const labels = Object.keys(categoryData);
    const data = Object.values(categoryData);
    
    const colors = [
        '#667eea', '#764ba2', '#f093fb', '#4facfe',
        '#43e97b', '#fa709a', '#fee140', '#30cfd0'
    ];
    
    categoryChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'right'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            return label + ': $' + value.toFixed(2);
                        }
                    }
                }
            }
        }
    });
}

// Format date for display
function formatDate(dateString) {
    const date = new Date(dateString + 'T00:00:00');
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Show success message
function showSuccess(message) {
    alert(message);
}

// Show error message
function showError(message) {
    alert(message);
}