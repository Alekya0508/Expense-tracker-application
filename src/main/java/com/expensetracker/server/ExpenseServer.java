package main.java.com.expensetracker.server;

import main.java.com.expensetracker.model.Expense;
import main.java.com.expensetracker.service.ExpenseManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Server that handles API requests and serves the frontend
 */
public class ExpenseServer {
    private static final int PORT = 8080;
    private static ExpenseManager expenseManager;

    public static void main(String[] args) throws IOException {
        expenseManager = new ExpenseManager();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // API endpoints
        server.createContext("/api/expenses", new ExpenseHandler());
        server.createContext("/api/analytics", new AnalyticsHandler());
        
        // Serve frontend files
        server.createContext("/", new FrontendHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Server started on port " + PORT);
        System.out.println("Open http://localhost:" + PORT + " in your browser");
    }

    /**
     * Handler for expense operations (GET, POST, DELETE)
     */
    static class ExpenseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            // Enable CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            try {
                if (method.equals("GET")) {
                    handleGetExpenses(exchange);
                } else if (method.equals("POST")) {
                    handleAddExpense(exchange);
                } else if (method.equals("DELETE")) {
                    handleDeleteExpense(exchange);
                } else {
                    sendResponse(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }

        private void handleGetExpenses(HttpExchange exchange) throws IOException {
            String json = expenseManager.expensesToJson();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            sendResponse(exchange, 200, json);
        }

        private void handleAddExpense(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            Map<String, String> data = parseJsonBody(body);
            
            String category = data.get("category");
            String amountStr = data.get("amount");
            String date = data.get("date");
            String description = data.get("description");
            
            if (category == null || amountStr == null || date == null) {
                sendResponse(exchange, 400, "Missing required fields");
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            Expense expense = expenseManager.addExpense(category, amount, date, description);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            sendResponse(exchange, 201, expense.toJson());
        }

        private void handleDeleteExpense(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            
            if (parts.length < 4) {
                sendResponse(exchange, 400, "Missing expense ID");
                return;
            }
            
            long id = Long.parseLong(parts[3]);
            boolean deleted = expenseManager.deleteExpense(id);
            
            if (deleted) {
                sendResponse(exchange, 200, "{\"message\":\"Expense deleted\"}");
            } else {
                sendResponse(exchange, 404, "{\"message\":\"Expense not found\"}");
            }
        }
    }

    /**
     * Handler for analytics endpoint
     */
    static class AnalyticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            
            String json = expenseManager.getAnalyticsJson();
            sendResponse(exchange, 200, json);
        }
    }

    /**
     * Handler to serve frontend files
     */
    static class FrontendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            if (path.equals("/")) {
                serveFile(exchange, "frontend/index.html", "text/html");
            } else if (path.equals("/style.css")) {
                serveFile(exchange, "frontend/style.css", "text/css");
            } else if (path.equals("/app.js")) {
                serveFile(exchange, "frontend/app.js", "application/javascript");
            } else {
                sendResponse(exchange, 404, "Not found");
            }
        }

        private void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
            File file = new File(filePath);
            
            if (!file.exists()) {
                sendResponse(exchange, 404, "File not found");
                return;
            }
            
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] bytes = fis.readAllBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        }
    }

    /**
     * Helper method to send HTTP response
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    /**
     * Helper method to read request body
     */
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    /**
     * Simple JSON parser for request body
     */
    private static Map<String, String> parseJsonBody(String json) {
        Map<String, String> data = new HashMap<>();
        
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }
        
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                data.put(key, value);
            }
        }
        
        return data;
    }
}