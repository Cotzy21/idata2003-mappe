package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Compatibility facade for writing CSV stock data.
 *
 * @deprecated use {@link CsvStockWriter} through the {@link StockDataWriter}
 *     interface for new code.
 */
@Deprecated(since = "1.0", forRemoval = false)
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
        new CsvStockWriter().write(file, stocks);
    }
}
