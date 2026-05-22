package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writes stock data to a CSV file.
 * CSV format: Symbol,Company,Price
 * Includes header comments.
 */
public class StockCsvWriter {
    /**
     * Private constructor to prevent instantiation.
     */
    private StockCsvWriter() {
        // Utility class - should not be instantiated
    }

    /**
     * Writes stocks to a CSV file.
     *
     * @param filePath the path to the output CSV file
     * @param stocks the list of stocks to write
     * @throws IOException if the file cannot be written
     */
    public static void writeStocks(String filePath, List<Stock> stocks) throws IOException {
        writeStocks(new File(filePath), stocks);
    }

    /**
     * Writes stocks to a CSV file.
     *
     * @param file the output CSV file
     * @param stocks the list of stocks to write
     * @throws IOException if the file cannot be written
     */
    public static void writeStocks(File file, List<Stock> stocks) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("# Stock Exchange Data");
            writer.newLine();
            writer.write("# Ticker,Name,Price");
            writer.newLine();
            writer.newLine();

            // Write stock data
            for (Stock stock : stocks) {
                writer.write(String.format("%s,%s,%s",
                        stock.getSymbol(),
                        stock.getCompany(),
                        stock.getSalesPrice()));
                writer.newLine();
            }
        }
    }
}

