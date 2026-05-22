package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads stock data from a CSV file.
 * CSV format: lines starting with '#' are comments, blank lines are ignored.
 * Data lines: Ticker,Name,Price (with price using period as decimal separator).
 */
public class StockCsvReader {
    /**
     * Private constructor to prevent instantiation.
     */
    private StockCsvReader() {
        // Utility class - should not be instantiated
    }

    /**
     * Reads stocks from a CSV file.
     *
     * @param filePath the path to the CSV file
     * @return a list of Stock objects
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a line has invalid format
     */
    public static List<Stock> readStocks(String filePath) throws IOException {
        return readStocks(new File(filePath));
    }

    /**
     * Reads stocks from a CSV file.
     *
     * @param file the CSV file
     * @return a list of Stock objects
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a line has invalid format
     */
    public static List<Stock> readStocks(File file) throws IOException {
        List<Stock> stocks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip comments and blank lines
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }

                try {
                    Stock stock = parseCsvLine(line);
                    stocks.add(stock);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Error parsing CSV at line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return stocks;
    }

    /**
     * Parses a single CSV line into a Stock object.
     * Format: Symbol,Company Name,Price
     *
     * @param line the CSV line
     * @return a Stock object
     * @throws IllegalArgumentException if the line format is invalid
     */
    private static Stock parseCsvLine(String line) {
        String[] parts = line.split(",");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Expected 3 fields (symbol, company, price), got " + parts.length);
        }

        String symbol = parts[0].trim();
        String company = parts[1].trim();
        String priceStr = parts[2].trim();

        if (symbol.isBlank() || company.isBlank()) {
            throw new IllegalArgumentException("Symbol and company name cannot be empty");
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + priceStr);
        }

        return new Stock(symbol, company, price);
    }
}

