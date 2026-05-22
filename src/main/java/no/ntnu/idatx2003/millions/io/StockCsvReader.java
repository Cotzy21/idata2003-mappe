package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.model.Stock;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Compatibility facade for reading CSV stock data.
 *
 * @deprecated use {@link CsvStockReader} through the {@link StockDataReader}
 *     interface for new code.
 */
@Deprecated(since = "1.0", forRemoval = false)
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
        try {
            return new CsvStockReader().read(file);
        } catch (InvalidStockDataException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }
}
